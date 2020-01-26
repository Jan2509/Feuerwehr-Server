package me.feuerwehr.notification.server

import com.google.common.annotations.Beta
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.uchuhimo.konf.ConfigSpec
import freemarker.ext.servlet.FreemarkerServlet
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.html.insert
import io.ktor.html.respondHtml
import io.ktor.html.respondHtmlTemplate
import io.ktor.http.CacheControl
import io.ktor.http.ContentType
import io.ktor.http.CookieEncoding
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.CachingOptions
import io.ktor.http.content.resource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.*
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.sessions.*
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.pipeline.PipelineContext
import kotlinx.html.body
import kotlinx.html.head
import kotlinx.html.unsafe
import me.feuerwehr.notification.server.database.dao.*
import me.feuerwehr.notification.server.database.table.*
import me.feuerwehr.notification.server.web.components.html.*
import me.feuerwehr.notification.server.web.components.json.EmptyJSON
import me.feuerwehr.notification.server.web.components.json.MessageJSON
import me.feuerwehr.notification.server.web.components.json.rest.*
import me.feuerwehr.notification.server.web.user.WebUserSession
import org.apache.commons.lang3.RandomStringUtils
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class NotificationServer constructor(
    private val configFile: File = File("config.yml")
) {
    private val logger: Logger = LoggerFactory.getLogger("FFServer")
    private val mdParser = Parser.builder().build()
    private val htmlRenderer = HtmlRenderer.builder()
        .build()
    @Beta
    private fun createUserPrincipalCache(database: Database) = CacheBuilder.newBuilder()
        .weakValues()
        .build<WebUserSession, UserPrincipal?>(object : CacheLoader<WebUserSession, UserPrincipal?>() {
            override fun load(key: WebUserSession) =
                when (val user = transaction(database) { WebUserDAO.findById(key.userID) }) {
                    null -> null
                    else -> UserPrincipal(key.userID, user)
                }
        })

    @KtorExperimentalAPI
    fun enable() {
        val config = initConfig()
        val database = createDatabase(config)
        initDatabase(database)

        val sessionStorageString = ""
        val sessionStorage = when {
            sessionStorageString.isBlank() -> SessionStorageMemory()
            else -> directorySessionStorage(File(sessionStorageString), true)
        }
        val webHost = config[ConnectionSpec.host]
        val webPort = config[ConnectionSpec.port]
        val webConfig = applicationEngineEnvironment {
            connector {
                this.host = webHost
                this.port = webPort
            }
            module {
                this.main(sessionStorage, database)
                this.loginPage()
                this.mainPages()
                this.api(database)
            }
        }
        val server = embeddedServer(Netty,  webConfig)
        server.start(wait = true)
    }

    @KtorExperimentalAPI
    fun Application.main(sessionStorage: SessionStorage, database: Database) {
        routing {
            static("assets") {
                resources("/web/assets")
                resource("favicon.ico", "/web/assets/meta/favicon.ico")
            }
        }
        install(CachingHeaders) {
            options { outgoingContent ->
                when (outgoingContent.contentType?.withoutParameters()) {
                    ContentType.Text.CSS -> CachingOptions(
                        CacheControl.MaxAge(
                            maxAgeSeconds = 24 * 60 * 60,
                            mustRevalidate = true
                        )
                    )
                    ContentType.Application.JavaScript -> CachingOptions(
                        CacheControl.MaxAge(
                            maxAgeSeconds = 24 * 60 * 60,
                            mustRevalidate = true
                        )
                    )
                    ContentType.Image.Any -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 24 * 60 * 60))
                    else -> null
                }
            }
        }
        install(Compression) {
            gzip {
                priority = 1.0
            }
            deflate {
                priority = 10.0
                minimumSize(1024) // condition
            }
        }

        install(ContentNegotiation) {
            jackson {

            }
        }
        install(Sessions) {
            cookie<WebUserSession>(
                "Feuerwehr-Login",
                sessionStorage
            ) {
                cookie.path = "/"
                cookie.encoding = CookieEncoding.URI_ENCODING
                cookie.maxAgeInSeconds = 14 * 24 * 60 * 60
                this.identity { RandomStringUtils.randomAlphabetic(64) }
            }
        }
        install(Authentication) {
            this.session<WebUserSession>("Feuerwehr-Login") {
                challenge("/login")
                validate { call -> createUserPrincipalCache(database)[call] }
            }

        }/*
        install(Freemarker) {
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        }*/
    }

    private fun Application.mainPages() {
        routing {
            authenticate("Feuerwehr-Login") {
                get("/") {
                    call.respondHtmlTemplate(OuterPage(), HttpStatusCode.Accepted) {

                        content {
                            insert(EinsatzContainer) {

                            }
                        }
                    }
                }
                get("/User") {
                    call.respondHtmlTemplate(OuterPage(), HttpStatusCode.Accepted) {
                        content{
                            insert(UserContainer) {}
                            insert(UserList) {}
                        }
                    }
                }

                get("/Einsaetze"){
                    call.respondHtmlTemplate(OuterPage(), HttpStatusCode.Accepted) {
                        content {
                            insert(EinsatzList) {

                            }
                        }
                    }
                }
                get("/Ausbildung") {
                    call.respondHtmlTemplate(OuterPage(), HttpStatusCode.Accepted) {
                        content{
                            insert(AusbildungsContainer) {}
                            insert(AusbildungsList) {}
                        }
                    }
                }

                get("/about") {
                    call.respondHtmlTemplate(OuterPage(), HttpStatusCode.Accepted) {
                        val doc =
                            mdParser.parseReader(java.io.InputStreamReader(this::class.java.getResourceAsStream("/web/md/about.md")))
                        content {
                            unsafe {
                                +htmlRenderer.render(doc)
                            }
                        }
                    }
                }
            }
        }
    }

    fun Application.loginPage() {
        routing {
            get("/login") {
                if (call.authentication.principal != null) {
                    call.respondRedirect("/", false)
                } else {
                    call.respondHtml {
                        head {
                            insert(DefaultPageHead) {}
                        }
                        body {
                            insert(LoginBoxPage) {}
                        }
                    }
                }
            }
        }
    }

    fun Application.api(database: Database) {
        routing {
            route("api") {
                apiInternal(database)
                apiApp(database)
            }
        }
    }
    fun Route.apiApp(database: Database) {
        route("app") {
            post("abfrage") {
                val alarmRequest = call.receive(AlarmRequestingJSON::class)
                val user = transaction(database) {
                    WebUserDAO.find { WebUserTable.username like alarmRequest.name }.firstOrNull()
                }
                if (user != null) {
                    val voralarm = transaction(database) {
                        WebEinsatzTable.slice(WebEinsatzTable.id).selectAll()
                            .orderBy(WebEinsatzTable.id, SortOrder.DESC).firstOrNull()
                    }
                    if (voralarm != null) {
                        val alarm = transaction(database) {
                            EinsatzTeilnahmeTable.join(
                                WebEinsatzTable,
                                joinType = JoinType.INNER,
                                additionalConstraint = { EinsatzTeilnahmeTable.EID eq WebEinsatzTable.id })
                                .slice(EinsatzTeilnahmeTable.EID, EinsatzTeilnahmeTable.MID).selectAll()
                                .having {
                                    (EinsatzTeilnahmeTable.EID eq voralarm[WebEinsatzTable.id]) and (EinsatzTeilnahmeTable.MID eq user.id)
                                }.firstOrNull()
                        }
                        if (alarm == null) {
                            val info = transaction(database) {
                                WebEinsatzTable.slice(
                                    WebEinsatzTable.id,
                                    WebEinsatzTable.stichwort,
                                    WebEinsatzTable.strasse,
                                    WebEinsatzTable.hausnr,
                                    WebEinsatzTable.plz,
                                    WebEinsatzTable.ort,
                                    WebEinsatzTable.bemerkungen
                                ).selectAll()
                                    .having { WebEinsatzTable.id eq voralarm[WebEinsatzTable.id] }.firstOrNull()
                            }
                            if (info != null) {
                                call.respond(
                                    AlarmResponseJSON(
                                        true,
                                        info[WebEinsatzTable.stichwort],
                                        info[WebEinsatzTable.strasse],
                                        info[WebEinsatzTable.hausnr],
                                        info[WebEinsatzTable.plz],
                                        info[WebEinsatzTable.ort],
                                        info[WebEinsatzTable.bemerkungen]
                                    )
                                )
                            }
                        } else {
                            call.respond(
                                AlarmResponseJSON(false, null, null, null, null, null, null)
                            )
                        }
                    } else {
                        call.respond(
                            AlarmResponseJSON(false, null, null, null, null, null, null)
                        )
                    }
                }
            }
            post("response") {
                val alarmResponse = call.receive(ReponseJSON::class)
                val user = transaction(database) {
                    WebUserDAO.find { WebUserTable.username like alarmResponse.username }.firstOrNull()
                }
                if (user != null) {
                    val voralarm = transaction(database) {
                        WebEinsatzTable.slice(WebEinsatzTable.id).selectAll()
                            .orderBy(WebEinsatzTable.id, SortOrder.DESC).firstOrNull()
                    }
                    if (voralarm != null) {
                        val alarm = transaction(database) {
                            EinsatzTeilnahmeTable.join(
                                WebEinsatzTable,
                                joinType = JoinType.INNER,
                                additionalConstraint = { EinsatzTeilnahmeTable.EID eq WebEinsatzTable.id })
                                .slice(EinsatzTeilnahmeTable.EID, EinsatzTeilnahmeTable.MID).selectAll()
                                .having {
                                    ((EinsatzTeilnahmeTable.EID eq voralarm[WebEinsatzTable.id])
                                    and
                                    (EinsatzTeilnahmeTable.MID eq user.id))
                                }.firstOrNull()
                        }
                        if (alarm == null) {
                            val info = transaction(database) {
                                WebEinsatzTable.slice(
                                    WebEinsatzTable.id
                                ).selectAll()
                                    .having { WebEinsatzTable.id eq voralarm[WebEinsatzTable.id] }.firstOrNull()
                            }
                            if (info != null) {
                                transaction(database) {
                                    EinsatzTeilnahmeDAO.new {
                                        EID = voralarm[WebEinsatzTable.id]
                                        MID = user.id
                                        teilgenommen = alarmResponse.success
                                    }
                                }
                                call.respond(
                                    CreateResponseJSON(
                                        true
                                    )
                                )
                            }
                        } else {
                            call.respond(
                                CreateResponseJSON(false)
                            )
                        }
                    } else if (voralarm == null) {
                        call.respond(
                            CreateResponseJSON(false)
                        )
                    }
                }
            }
        }
    }

                /* val alarmResponse = call.receive(ReponseJSON::class)
                if (user != null){
                    transaction(database) {
                        val einsatz = EinsatzTeilnahmeDAO.find { EinsatzTeilnahmeTable.id eq WebEinsatzTable.id.max()}.firstOrNull()
                        EinsatzTeilnahmeDAO.new {
                            if (einsatz != null) {
                                EID = einsatz.id
                                MID = user.id
                                teilgenommen = alarmResponse.response
                            }
                        }

                    }
                    call.respond(
                        CreateResponseJSON(true )
                    )
                } else {
                    call.respond(
                        CreateResponseJSON(false)
                    )
                }

            }
        }*/

    fun Route.apiInternal(database: Database) {
        route("internal") {
            get("alarmdisplay") {
                call.respondHtml {
                    head {
                        insert(DefaultPageHead) {}
                    }
                    body {
                        insert(AlarmResponse) {}
                    }
                }
            }
            post("login") {
                //delay(Duration.ofSeconds(3))
                runCatching {
                    if (validSession(database)) {
                        call.respond(HttpStatusCode.BadRequest, "you already logged in")
                    } else {
                        val request = call.receive(LoginRequestingJSON::class)
                        val user = transaction(database) {
                            WebUserDAO.find { WebUserTable.username like request.username }.firstOrNull()
                        }
                        if (user != null && user.hasPassword(request.password)) {
                            call.sessions.set(WebUserSession(user.id.value))
                            call.respond(
                                LoginResponseJSON(
                                    true
                                )
                            )
                        } else {
                            call.respond(
                                LoginResponseJSON(
                                    false
                                )
                            )
                        }
                    }
                }.getOrElse {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
            authenticate("Feuerwehr-Login") {
                get("logout") {

                    call.sessions.clear<WebUserSession>()
                    call.respond(MessageJSON("logged out"))
                }
                get("sessionInfo") {
                    val session = call.sessions.get<WebUserSession>()
                    call.respond(session ?: EmptyJSON)
                }
                post("add") {
                    //delay(Duration.ofSeconds(3))
                    //runCatching {
                    val addRequest = call.receive(addRequestingJSON::class)
                    val user = transaction(database) {WebUserDAO.find { WebUserTable.username like addRequest.username }.firstOrNull() }
                    val ausbildung = transaction(database) { WebAusbildungsDAO.find { WebAusbildungsTable.bezeichnung like addRequest.bezeichnung}.firstOrNull()}
                    if (ausbildung != null && user != null) {
                        val ausNotExists: Boolean = transaction(database) {
                            AusgebildetDAO.find { AusgebildetTable.AID eq ausbildung.id and (AusgebildetTable.MID eq user.id) }.empty()
                        }
                        if (ausNotExists) {
                            if (user != null) {
                                transaction(database) {
                                    AusgebildetDAO.new {
                                        MID = user.id
                                        AID = ausbildung.id
                                    }
                                }
                            }
                            call.respond(
                                CreateResponseJSON(
                                    true
                                )
                            )
                        } else {
                            call.respond(
                                CreateResponseJSON(
                                    false
                                )
                            )
                        }
                        //}.getOrElse {
                        //    call.respond(HttpStatusCode.InternalServerError)
                        //}
                    }
                }
                post("create") {
                    //delay(Duration.ofSeconds(3))
                    //runCatching {
                        val createRequest = call.receive(CreateRequestingJSON::class)
                        val userNotExists : Boolean = transaction(database) {
                            WebUserDAO.find { WebUserTable.username like createRequest.username }.empty()
                        }
                        if (userNotExists) {
                            val einnew : String = (SimpleDateFormat("dd.MM.yyyy").format(createRequest.ein)).toString()
                            val gebnew: String = (SimpleDateFormat("dd.MM.yyyy").format(createRequest.geb)).toString()
                            transaction(database) {
                                WebUserDAO.new {
                                    username = createRequest.username
                                    vorname = createRequest.vorname
                                    setPassword(createRequest.password)
                                    tel = createRequest.tel
                                    geb = gebnew
                                    ein = einnew
                                }
                            }
                            call.respond(
                                CreateResponseJSON(
                                    true
                                )
                            )
                        } else {
                            call.respond(
                                CreateResponseJSON(
                                    false
                                )
                            )
                        }
                    //}.getOrElse {
                    //    call.respond(HttpStatusCode.InternalServerError)
                    //}
                }
                post("createEinsatz") {
                    //delay(Duration.ofSeconds(3))
                    //runCatching {
                        val createRequest = call.receive(CreateEinsatzRequestingJSON::class)
                        val date : String = alarmdate()
                        val time : String = alarmtime()
                        transaction(database) {
                            WebEinsatzDAO.new {
                                stichwort = createRequest.stichwort
                                strasse = createRequest.strasse
                                hausnr = createRequest.hausnr
                                plz= createRequest.plz
                                ort = createRequest.ort
                                datum = date
                                zeit = time
                                bemerkungen = createRequest.bemerkungen
                            }
                        }
                        call.respond(
                            CreateResponseJSON(
                                true
                            )
                        )
                    //}.getOrElse {
                    //    call.respond(HttpStatusCode.InternalServerError)
                    //}
                }
            }

        }
    }

    private fun initConfig(): Konf = ConfigWrapper(Konf {
        listOf(
            ConnectionSpec,
            DatabaseSpec
        ).forEach { spec ->
            addSpec(spec)
        }
    }).also { config ->
        if (configFile.exists())
            config.load(configFile)
        else {
            configFile.parentFile?.mkdirs()
            config.save(configFile)
        }
    }
    private fun getTimeMilis(): Long{
        val timestamp1 = Calendar.getInstance()
        return timestamp1.timeInMillis
    }
    private fun alarmdate(): String {
        val formatter = SimpleDateFormat("dd.MM.yyyy")
        val time = getTimeMilis()
        return formatter.format(time)
    }
    private fun alarmtime(): String {
        val formatter = SimpleDateFormat("HH:mm:ss")
        val time = getTimeMilis()
        return formatter.format(time)
    }
    private fun initDatabase(database: Database) = transaction(database) {
        SchemaUtils.createMissingTablesAndColumns(WebUserTable)
        SchemaUtils.createMissingTablesAndColumns(WebEinsatzTable)
        SchemaUtils.createMissingTablesAndColumns(EinsatzTeilnahmeTable)
        SchemaUtils.createMissingTablesAndColumns(WebAusbildungsTable)
        SchemaUtils.createMissingTablesAndColumns(AusgebildetTable)
        if (!WebUserTable.selectAll().any()) {
            val password = RandomStringUtils.randomAlphabetic(8)
            val user = "admin"
            val date : String = alarmdate()
            WebUserDAO.new {
                username = user
                vorname = ""
                setPassword(password)
                tel = " "
                geb = date
                ein = date

            }
            logger.info("Das ist dein $user Passwort: $password")
        }
    }

    private fun PipelineContext<*, ApplicationCall>.validSession(database: Database) = runCatching {
        val session = call.sessions.get<WebUserSession>()
        return@runCatching session != null && transaction(database) { WebUserDAO.findById(session.userID) != null }
    }.getOrElse {
        false
    }

    private fun createDatabase(config: Konf) = Database.connect(
        "jdbc:mysql://" +
                config[DatabaseSpec.dataHost] +
                ":" + config[DatabaseSpec.dataPort] + "/" + config[DatabaseSpec.database],
        driver = "com.mysql.jdbc.Driver",
        user = config[DatabaseSpec.dataUser],
        password = config[DatabaseSpec.dataPass]
    )

}

//object ServerSpec : ConfigSpec("") {
object ConnectionSpec : ConfigSpec("Connection") {
    val host by optional("127.0.0.1", "Host")
    val port by optional(8080, "Port")
}

object DatabaseSpec : ConfigSpec("Database") {
    val dataHost by optional("localhost", "Host")
    val dataPort by optional("3306", "Port")
    val dataUser by optional("root", "User")
    val dataPass by optional("", "Password")
    val database by optional("", "Database")
}

//}
private data class UserPrincipal(val id: Int, val user: WebUserDAO) : Principal {
    companion object Static {
        private val logger = LoggerFactory.getLogger(UserPrincipal::class.java)
    }

    init {
        logger.debug("Init (id=$id user=${user.username})")
    }
}