package me.feuerwehr.notification.server.database.dao

import me.feuerwehr.notification.server.database.table.AusgebildetTable
import me.feuerwehr.notification.server.database.table.WebAusbildungsTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class AusgebildetDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object Static : IntEntityClass<AusgebildetDAO>(
        AusgebildetTable
    )
    var MID by AusgebildetTable.MID
    var AID by AusgebildetTable.AID
}