package me.feuerwehr.notification.server.web.components.html

import io.ktor.html.Placeholder
import io.ktor.html.Template
import io.ktor.html.insert
import kotlinx.html.*

class OuterPage() : Template<HTML> {
    val content = Placeholder<DIV>()
    override fun HTML.apply() {

        attributes["data-barba"] = "wrapper"
        head {
            insert(DefaultPageHead) {}

        }
        body("has-navbar-fixed-top ") {
            topNavBar()
            main("container is-fluid") {
                attributes["data-barba"] = "container"
                div("notification") {
                    insert(content)
                }
            }
            script {
                +"onOuterPageFinish()"
            }
        }
    }

    private fun BODY.topNavBar() {
        nav("navbar is-info is-fixed-top") {
            role = "navigation"

            div("navbar-brand") {
                div("navbar-item") {
                    +"Feuerwehr Information Service"
                }

                a(classes = "navbar-burger burger") {
                    role = "button"
                    id = "mobile-top-menu-burger"
                    repeat(3) { span { } }
                }
            }

            div("navbar-menu") {
                id = "main-top-menu"
                div("navbar-start") {

                    a("/", classes = "navbar-item") {
                        +"Home"
                    }
                    a("/Einsaetze", classes = "navbar-item") {
                        +"Einsätze"
                    }
                    a("/User", classes = "navbar-item") {
                        +"User"
                    }
                    a("/Ausbildung", classes = "navbar-item") {
                        +"Ausbildungen"
                    }
                }
                div("navbar-end") {

                    div("navbar-item has-dropdown is-hoverable") {
                        a(classes = "navbar-link") {
                            +"Account"
                        }
                        div("navbar-dropdown") {
                            a(classes = "navbar-item") {
                                onClick = "onLogoutInput()"
                                +"Logout"
                            }
                        }
                    }
                }
            }
        }
    }
}