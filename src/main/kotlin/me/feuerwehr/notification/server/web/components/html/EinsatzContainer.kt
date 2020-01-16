package me.feuerwehr.notification.server.web.components.html

import io.ktor.html.Template
import kotlinx.html.*

object EinsatzContainer : Template<DIV> {
    override fun DIV.apply() {
        form {
            id = "create-einsatz-form"
            div("field") {
                label("label") { +"Stichwort" }
                div("select is-fullwidth") {
                    select{
                        id = "create-stichwort-input"
                        option{+"Feuer 1"}
                        option{+"Feuer 2"}
                        option{+"Feuer 3"}
                        option{+"Feuer 1 Person"}
                        option{+"Feuer 2 Person"}
                        option{+"Feuer 3 Person"}
                        option{+"Feuer 1 BMA"}
                        option{+"Feuer 2 BMA"}
                        option{+"Feuer 3 BMA"}
                        option{+"Feuer Kamin"}
                        option{+"Feuer Dach"}
                        option{+"Feuer Keller"}
                        option{+"Feuer 1 PKW"}
                        option{+"Feuer 2 PKW"}
                        option{+"Feuer 1 LKW"}
                        option{+"Feuer 2 LKW"}
                        option{+"Feuer 1 Wald"}
                        option{+"Feuer 2 Wald"}
                        option{+"Feuer Explosion"}
                        option{+"TH HiLoPe"}
                        option{+"TH Bombe"}
                        option{+"TH Ölspur"}
                        option{+"TH Bahn"}
                        option{+"TH 1 Einsturz"}
                        option{+"TH 2 Einsturz"}
                        option{+"TH Person"}
                        option{+"TH klein"}
                        option{+"TH groß"}
                        option{+"TH P-klemmt 1"}
                        option{+"TH P-klemmt 2"}
                        option{+"TH 1 Gas"}
                        option{+"TH 2 Gas"}
                        option{+"TH ABC 1"}
                        option{+"TH ABC 2"}
                        option{+"TH ABC 3"}
                        option{+"TH ABC 3 Feuer"}
                        option{+"TH ABC 4 Person"}
                        option{+"TH ABC 4 F+ Person"}
                        option{+"TH ABC 4 + V-Dekon"}
                        option{+"TH V-Dekon"}
                        option{+"TH Wasser"}
                        option{+"TH Wasser Person"}
                    }
                }
            }
            div("field") {
                label("label") { +"Strasse" }
                div("control") {
                    input(type = InputType.password, classes = "input") {
                        id = "create-strasse-input"
                        placeholder = "Strasse"
                    }
                }
            }
            div("field") {
                label("label") { +"Haus Nr" }
                div("control") {
                    input(type = InputType.password, classes = "input") {
                        id = "create-strasse-input"
                        placeholder = "Haus Nr"
                    }
                }
            }
            div("field") {
                label("label") { +"Postleitzahl" }
                div("control") {
                    input(type = InputType.password, classes = "input") {
                        id = "create-plz-input"
                        placeholder = "Postleitzahl"
                    }
                }
            }
            div("field") {
                label("label") { +"Ort" }
                div("control") {
                    input(type = InputType.password, classes = "input") {
                        id = "create-strasse-input"
                        placeholder = "Ort"
                    }
                }
            }
            div("field") {
                label("label") { +"Bemerkungen" }
                div("control") {
                    input(type = InputType.password, classes = "input") {
                        id = "create-bemerkungen-input"
                        placeholder = "Bemerkungen"
                    }
                }
            }
            div("control") {
                button(classes = "button is-success is-light is-fullwidth") {
                    id = "create-btn"
                    type = ButtonType.submit
                    + "ALARM"

                }
            }
        }
        //script { +"createEinsatzPage()" }
    }
}