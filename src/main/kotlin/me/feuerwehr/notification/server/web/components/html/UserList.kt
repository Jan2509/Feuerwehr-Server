package me.feuerwehr.notification.server.web.components.html

import io.ktor.html.Template
import kotlinx.html.*

object UserList : Template<DIV> {
    override fun DIV.apply() {
        script { +"userPage()" }
        table(classes = "display") {
            id = "example"
            thead{
                tr{
                    th{+"Name"}
                    th{+"Position"}
                    th{+"Office"}
                    th{+"Age"}
                    th{+"Start date"}
                    th{+"Salary"}
                }
            }
            tbody{
                tr {
                    td{+"Tiger Nixon"}
                    td{+"System Architect"}
                    td{+"Edinburgh"}
                    td{+"61"}
                    td{+"2011/04/25"}
                    td{+"$320,800"}
                }
            }
        }
    }
}