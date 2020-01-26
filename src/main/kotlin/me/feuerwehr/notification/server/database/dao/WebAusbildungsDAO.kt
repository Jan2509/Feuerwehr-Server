package me.feuerwehr.notification.server.database.dao

import me.feuerwehr.notification.server.database.table.WebAusbildungsTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class WebAusbildungsDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object Static : IntEntityClass<WebAusbildungsDAO>(
        WebAusbildungsTable
    )
    var bezeichnung by WebAusbildungsTable.bezeichnung
}