package me.feuerwehr.notification.server.database.table

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object EinsatzTeilnahmeTable : IntIdTable("Teilnahme") {
    override val primaryKey = PrimaryKey(id, name =  "TID")
    val EID = entityId("EID", WebEinsatzTable)
    val MID  = entityId("MID", WebEinsatzTable)
    val teilgenommen = bool("Teilgenommen")
}