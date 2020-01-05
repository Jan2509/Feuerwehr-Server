package me.feuerwehr.notification.server

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.html.insert
import io.ktor.html.respondHtml
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
import me.feuerwehr.notification.server.database.dao.WebUserDAO
import me.feuerwehr.notification.server.database.table.WebUserTable
import me.feuerwehr.notification.server.html.DefaultPageHead
import me.feuerwehr.notification.server.html.LoginBoxPage
import me.feuerwehr.notification.server.web.json.rest.LoginRequestingJSON
import me.feuerwehr.notification.server.web.json.rest.LoginResponseJSON
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun main() {
    val webconfig = applicationEngineEnvironment {
        connector {
            host = "127.0.0.1"
            port = 8080
        }
        module {
            main()
            loginPage()
            api()
        }
    }
    val server = embeddedServer(Netty, webconfig)
    server.start(wait = true)
}

fun Application.main() {
    routing {
        static("assets") {
            resources("/web/assets")
            //resource("favicon.ico", "/web/assets/meta/favicon.ico")
        }
    }
    install(Sessions) {
        cookie<LoginSession>(
            "FeuerwehrWebLoginSession", SessionStorageMemory()
        ) {
            cookie.path = "/"
        }
    }
    install(Authentication) {
        session<LoginSession> {
            challenge("/login")
            validate { session -> getUserbyUUID(session.userID) }
        }
    }
}
fun Application.loginPage(){
    routing {
        get ("/login"){
            if (call.authentication.principal != null)
            {
                call.respondRedirect("/", false)
            }
            else {
                call.respondHtml{
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
fun Application.api () {
    routing() {
        route("api") {
            apiinternal()
        }
    }
}
fun Route.apiinternal(){
    route("internal"){
        post ("login"){
            runCatching {
                if (true) {
                    call.respond(HttpStatusCode.BadRequest, "you already logged in")
                } else {
                    val request = call.receive(LoginRequestingJSON::class)
                    val user = transaction(DbSettings.db) {
                        WebUserDAO.find { WebUserTable.username like request.username }.firstOrNull()
                    }
                    if (user != null && user.hasPassword(request.password)) {
                        call.sessions.set(LoginSession(1))
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
    }
}
object DbSettings {
    val db = Database.connect("jdbc:mysql://localhost:3306/test", driver = "com.mysql.jdbc.Driver",
    user = "root", password = "your_pwd")
}
data class LoginSession(val userID : Int){

}
fun getUserbyUUID (userid : Int): Principal? {
    return UserIdPrincipal(userid.toString())
}