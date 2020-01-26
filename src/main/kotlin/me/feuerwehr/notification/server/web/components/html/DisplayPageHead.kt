package me.feuerwehr.notification.server.web.components.html

import io.ktor.html.Template
import kotlinx.html.HEAD
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.script

object DisplayPageHead: Template<HEAD> {
    private const val PAGE_META_ASSETS_PATH = "/assets/meta/"
    override fun HEAD.apply() {
        meta{ content = "5"; httpEquiv="refresh"}

        link(rel = "stylesheet", href = "/assets/main.css") { }
        script(src = "/assets/main.js") { }
    }


}