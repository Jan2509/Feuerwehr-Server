package me.feuerwehr.notification.server.web.components.html

import io.ktor.html.Template
import kotlinx.html.*

object UserContainer : Template<DIV> {
        override fun DIV.apply() {
            form {
                id = "create-form"
                div("field") {
                    label("label") { +"NachNnme" }
                    div("control") {
                        input(type = InputType.text, classes = "input") {
                            id = "create-username-input"
                            placeholder = "Nachname"
                        }
                    }
                }
                div("field") {
                    label("label") { +"Vorname" }
                    div("control") {
                        input(type = InputType.text, classes = "input") {
                            id = "create-vorname-input"
                            placeholder = "Vorname"
                        }
                    }
                }
                div("field") {
                    label("label") { +"Password" }
                    div("control") {
                        input(type = InputType.password, classes = "input") {
                            id = "create-password-input"
                            placeholder = "Password"
                        }
                    }
                }
                div("field") {
                    label("label") { +"Telefonnummer" }
                    div("control") {
                        input(type = InputType.text, classes = "input") {
                            id = "create-tel-input"
                            placeholder = "Telefonnummer"
                        }
                    }
                }
                div("field") {
                    label("label") { +"Geburtsdatum" }
                    div("control") {
                        input(type = InputType.date, classes = "input") {
                            id = "create-geb-input"
                            placeholder = "Geburtsdatum"
                        }
                    }
                }
                div("field") {
                    label("label") { +"Eintrittsdatum" }
                    div("control") {
                        input(type = InputType.date, classes = "input") {
                            id = "create-ein-input"
                            placeholder = "Eintrittsdatum"
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