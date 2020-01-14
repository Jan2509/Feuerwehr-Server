package me.feuerwehr.notification.server.database.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.datetime

object WebEinsatzTable : IntIdTable("Einsaetze") {
    override val primaryKey = PrimaryKey(id, name = "E_ID")
    val stichwort = text("Stichwort")
    val plz = varchar("PLZ", 5)
    val ort = varchar("Ort", 100)
    val strasse = varchar("Strasse", 100)
    val hausnr = varchar("HausNr", 1000)
    val datum = datetime("Datum").nullable()
}