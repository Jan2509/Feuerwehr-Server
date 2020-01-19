package me.feuerwehr.notification.server.web.components.json.rest

data class AlarmResponseJSON(
    val alarm : Boolean,
    val stichwort : String?,
    val strasse : String?,
    val hausnr : String?,
    val plz : String?,
    val ort : String?,
    val bemerkungen : String?
)