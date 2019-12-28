package me.feuerwehr.notification.server.html

import io.ktor.html.Template
import kotlinx.html.*

object DefaultPageHead : Template<HEAD> {
    private const val PAGE_META_ASSETS_PATH = "/assets/meta/"
    override fun HEAD.apply() {
        link(rel = "stylesheet", href = "/assets/main.css") { }
        script(src = "/assets/main.js") { }
    }


}