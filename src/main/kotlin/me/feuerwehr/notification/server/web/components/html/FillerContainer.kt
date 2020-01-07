package me.feuerwehr.notification.server.web.components.html
import io.ktor.html.Template
import kotlinx.html.DIV
import kotlinx.html.h1

object FillerContainer : Template<DIV> {
    override fun DIV.apply() {
        repeat(100) {
            h1 {
                +"Test-$it"
            }
        }
    }
}