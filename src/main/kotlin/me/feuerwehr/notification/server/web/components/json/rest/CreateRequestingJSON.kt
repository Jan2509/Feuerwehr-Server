package me.feuerwehr.notification.server.web.components.json.rest

import java.sql.Date
import java.time.LocalDateTime


data class CreateRequestingJSON(
    val username: String,
    val vorname: String,
    val password: String,
    val tel: String,
    val geb: Date,
    val ein: Date

)
