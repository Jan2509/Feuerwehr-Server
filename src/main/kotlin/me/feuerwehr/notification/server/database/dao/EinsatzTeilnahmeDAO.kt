package me.feuerwehr.notification.server.database.dao

import me.feuerwehr.notification.server.database.table.EinsatzTeilnahmeTable
import me.feuerwehr.notification.server.database.table.WebEinsatzTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class EinsatzTeilnahmeDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object Static : IntEntityClass<EinsatzTeilnahmeDAO>(
        EinsatzTeilnahmeTable
    )
    var EID by EinsatzTeilnahmeTable.EID
    var MID by EinsatzTeilnahmeTable.MID
    var teilgenommen by EinsatzTeilnahmeTable.teilgenommen
}