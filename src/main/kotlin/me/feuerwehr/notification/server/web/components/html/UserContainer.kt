package me.feuerwehr.notification.server.web.components.html

import io.ktor.html.Template
import kotlinx.html.*

object UserContainer : Template<DIV> {
        override fun DIV.apply() {
            form {
                id = "create-form"
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
                        id = "create-btn"
                        type = ButtonType.submit
                        + "Create"

                    }
                }
            }
            script { +"createPage()" }
        }
    }