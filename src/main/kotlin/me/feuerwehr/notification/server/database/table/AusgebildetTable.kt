package me.feuerwehr.notification.server.database.table

import org.jetbrains.exposed.dao.id.IntIdTable

object AusgebildetTable : IntIdTable("Ausgebildet") {
    override val primaryKey = PrimaryKey(EinsatzTeilnahmeTable.id, name =  "AID")
    val MID = entityId("MID", WebUserTable)
    val AID = entityId("AID", WebAusbildungsTable)
}