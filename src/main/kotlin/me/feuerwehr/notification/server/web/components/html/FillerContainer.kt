package me.feuerwehr.notification.server.web.components.html
import io.ktor.html.Template
import kotlinx.html.*

object FillerContainer : Template<DIV> {
    override fun DIV.apply() {
        table{
            tr {
                th{
                    +"Einsatz Nr"
                }
                th{
                    +"Stichwort"
                }
                th{
                    +"Beschreibung"
                }
            }
            repeat(100) {
                tr{
                    td { +"$it" }
                    td { +"Feuer $it" }
                    td {}
                }
            }
        }
        script {  }

    }
}