package me.feuerwehr.notification.server.database.table

import org.jetbrains.exposed.dao.id.IntIdTable

object WebAusbildungsTable : IntIdTable("Ausbildungen") {
    override val primaryKey = PrimaryKey(EinsatzTeilnahmeTable.id, name =  "AID")
    val bezeichnung = varchar("Bezeichnung", 100)
}