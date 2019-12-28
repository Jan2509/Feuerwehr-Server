package me.feuerwehr.notification.server.html

import io.ktor.html.Template
import kotlinx.html.*

object LoginBoxPage : Template<BODY>{
    override fun BODY.apply(){
        classes = setOf("login_body")
        id = "login_body"
        div(classes = "login_div") {
            form {
                id = "login-form"
                div("field") {
                    label("label") { +"Username" }
                    div("control") {
                        input(type = InputType.text, classes = "input") {
                            id = "username-input"
                            placeholder = "Username"
                        }
                    }

                }
                div("field") {
                    label("label") { +"Password" }
                    div("control") {
                        input(type = InputType.password, classes = "input") {
                            id = "password-input"
                            placeholder = "Password"
                        }
                    }
                }
                div("control") {
                    button(classes = "button is-success is-light is-fullwidth") {
                        id = "login-btn"
                        type = ButtonType.submit
                        + "Login"

                    }
                }
            }
        }
        script {
            +"loginPage()"
        }
    }
}