package me.feuerwehr.notification.server.database.dao

import me.feuerwehr.notification.server.database.table.WebEinsatzTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.time.LocalDateTime

class WebEinsatzDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object Static : IntEntityClass<WebEinsatzDAO>(
        WebEinsatzTable
    )
    var stichwort by WebEinsatzTable.stichwort
    var strasse by WebEinsatzTable.strasse
    var hausnr by WebEinsatzTable.hausnr
    var plz by WebEinsatzTable.plz
    var ort by WebEinsatzTable.ort
    var datum by WebEinsatzTable.datum
    var zeit by WebEinsatzTable.zeit
    var bemerkungen by WebEinsatzTable.bemerkungen
}