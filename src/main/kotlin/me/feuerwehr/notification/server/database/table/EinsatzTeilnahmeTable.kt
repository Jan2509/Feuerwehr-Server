package me.feuerwehr.notification.server.database.table

import org.jetbrains.exposed.dao.id.IntIdTable

object EinsatzTeilnahmeTable : IntIdTable("Teilnahme") {
    override val primaryKey = PrimaryKey(id, name =  "TID")
    val EID = integer("EID").references(WebEinsatzTable.id)
    val MID = integer("MID").references(WebUserTable.id)
    val teilgenommen = bool("Teilgenommen")
}