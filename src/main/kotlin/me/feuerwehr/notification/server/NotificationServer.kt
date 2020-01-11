package me.feuerwehr.notification.server

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.uchuhimo.konf.ConfigSpec
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.html.insert
import io.ktor.html.respondHtml
import io.ktor.http.CookieEncoding
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.*
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.sessions.*
import kotlinx.html.body
import kotlinx.html.head
import me.feuerwehr.notification.server.web.user.WebUserSession
import me.feuerwehr.notification.server.database.dao.WebUserDAO
import me.feuerwehr.notification.server.database.table.WebUserTable
import me.feuerwehr.notification.server.web.components.html.DefaultPageHead
import me.feuerwehr.notification.server.web.components.html.LoginBoxPage
import me.feuerwehr.notification.server.web.components.json.rest.LoginRequestingJSON
import me.feuerwehr.notification.server.web.components.json.rest.LoginResponseJSON
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import io.ktor.application.ApplicationCall
import io.ktor.features.*
import io.ktor.html.respondHtmlTemplate
import io.ktor.http.CacheControl
import io.ktor.http.ContentType
import io.ktor.http.cio.websocket.pingPeriod
import io.ktor.http.cio.websocket.timeout
import io.ktor.http.content.CachingOptions
import io.ktor.jackson.jackson
import io.ktor.util.pipeline.PipelineContext
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import kotlinx.html.unsafe
import me.feuerwehr.notification.server.web.components.html.FillerContainer
import me.feuerwehr.notification.server.web.components.html.OuterPage
import me.feuerwehr.notification.server.web.components.json.EmptyJSON
import me.feuerwehr.notification.server.web.components.json.MessageJSON
import org.apache.commons.lang3.RandomStringUtils
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.selectAll
import org.slf4j.LoggerFactory
import java.io.File
import java.time.Duration


class NotificationServer constructor(
    private val config: Konf = Konf{(addSpec(ServerSpec))},
    private val configFile: File = File("config")
    )  {
    private lateinit var database: Database
    private val mdParser = Parser.builder().build()
    private val htmlRenderer = HtmlRenderer.builder()
        .build()
    private val userPrincipalCache = CacheBuilder.newBuilder()
        .weakValues()
        .build<WebUserSession, UserPrincipal?>(object : CacheLoader<WebUserSession, UserPrincipal?>() {
            override fun load(key: WebUserSession) =
                when (val user = transaction(database) { WebUserDAO.findById(key.userID) }) {
                    null -> null
                    else -> UserPrincipal(key.userID, user)
                }
        })
    fun enable() {
        initConfig()
        DbSettings.setConfig(config)
        database = DbSettings.db
        initDatabase()

        val sessionStorageString = ""
        val sessionStorage = when {
            sessionStorageString.isBlank() -> SessionStorageMemory()
            else -> directorySessionStorage(File(sessionStorageString),true)
        }
        val webconfig = applicationEngineEnvironment {
            connector {
                host = "127.0.0.1"
                port = 8080
            }
            module {
                main(sessionStorage)
                loginPage()
                mainPages()
                api()
            }
        }
        val server = embeddedServer(Netty, webconfig)
        server.start(wait = true)
    }

    fun Application.main(sessionStorage: SessionStorage) {
        routing {
            static("assets") {
                resources("/web/assets")
                //resource("favicon.ico", "/web/assets/meta/favicon.ico")
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
        install(WebSockets) {
            pingPeriod = Duration.ofSeconds(10)
            timeout = Duration.ofSeconds(30)
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
                validate { call -> userPrincipalCache[call] }
            }

        }
    }
    private fun Application.mainPages() {
        routing {
            authenticate("Feuerwehr-Login") {
                get("/") {
                    call.respondHtmlTemplate(OuterPage(), HttpStatusCode.Accepted) {

                        content {
                            insert(FillerContainer) {

                            }
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

    fun Application.api() {
        routing() {
            route("api") {
                apiInternal()
            }
        }
    }

    private fun Route.apiInternal() {
        route("internal") {
            post("login") {
                //delay(Duration.ofSeconds(3))
                runCatching {
                    if (validSession()) {
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
                webSocket {
                    val webUserSession = call.sessions.get<WebUserSession>() ?: run {

                        return@webSocket
                    }

                }
            }

        }
    }

    private fun initConfig(){
        if (configFile.exists())
            config.load(configFile)
        else {
            configFile.parentFile?.mkdirs()
            config.save(configFile)
        }
    }
    private fun initDatabase() = transaction(database) {
        SchemaUtils.createMissingTablesAndColumns(WebUserTable)
        if (!WebUserTable.selectAll().any()) {
            val password = RandomStringUtils.randomAlphabetic(8)
            val user = "admin"
            WebUserDAO.new {
                username = user
                setPassword(password)
            }
            println("[Info] Das ist dein "+ user +" Passwort: " + password)
        }
    }
    private fun PipelineContext<*, ApplicationCall>.validSession() = runCatching {
        val session = call.sessions.get<WebUserSession>()
        return@runCatching session != null && transaction(database) { WebUserDAO.findById(session.userID) != null }
    }.getOrElse {
        false
    }
}
object ServerSpec : ConfigSpec("Connection") {
    val host by optional("127.0.0.1", "Server Host")
    val port by optional<Int>(8080,"Server Port")

    object Database : ConfigSpec("Database") {
        val dataHost by optional("127.0.0.1", "Datenbank Host")
        val dataPort by optional("3306", "Datenbank Port")
        val dataUser by optional("root", "Datenbank User")
        val dataPass by required<String>(null, "Datenbank Password")
        val database by required<String>(null, "Datenbank Password")
    }

}
object DbSettings {
    private lateinit var config: Konf
    fun setConfig(config:Konf){
        this.config = config
    }
    val db = Database.connect("jdbc:mysql://"+config[ServerSpec.Database.dataHost]+":"+config[ServerSpec.Database.dataPort]+"/"+config[ServerSpec.Database.database], driver = "com.mysql.jdbc.Driver",
    user = config[ServerSpec.Database.dataUser], password = config[ServerSpec.Database.dataPass])
}
private data class UserPrincipal(val id: Int, val user: WebUserDAO) : Principal {
    companion object Static {
        private val logger = LoggerFactory.getLogger(UserPrincipal::class.java)
    }

    init {
        logger.debug("Init (id=$id user=${user.username})")
    }
}