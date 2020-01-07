package me.feuerwehr.notification.server.web

import com.uchuhimo.konf.ConfigSpec

object WebConfigSpec : ConfigSpec("Web") {
    val XFORWARD_SUPPORT by optional(true, "EnableX-ForwardedSupport")

    object Session : ConfigSpec("Session") {
        val SESSION_STORAGE by optional("", "SessionStorage")
        val COOKIE_NAME by optional("orpheus-user", "CookieName")
        val LIFETIME by optional(14 * 24 * 60 * 60, "Lifetime")
    }
}

object WebThreadingConfigSpec : ConfigSpec("//TODO") {
}