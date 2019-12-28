package me.feuerwehr.notification.server

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.html.insert
import io.ktor.html.respondHtml
import io.ktor.http.CookieEncoding
import io.ktor.http.content.resource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.*
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.sessions.*
import kotlinx.html.body
import kotlinx.html.head
import me.feuerwehr.notification.server.html.DefaultPageHead
import me.feuerwehr.notification.server.html.LoginBoxPage
import java.io.File
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
data class LoginSession(val userID : UUID){

}
fun getUserbyUUID (userid : UUID): Principal? {
    return UserIdPrincipal(userid.toString())
}