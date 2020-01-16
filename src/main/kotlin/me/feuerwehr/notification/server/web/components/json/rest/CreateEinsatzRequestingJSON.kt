package me.feuerwehr.notification.server.web.components.json.rest

import java.util.*


data class CreateEinsatzRequestingJSON(
    val stichwort : String,
    val strasse : String,
    val hausnr : String,
    val plz : String,
    val ort : String,
    val bemerkungen : String
)