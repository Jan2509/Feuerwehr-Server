package me.feuerwehr.notification.server.database.table

import org.jetbrains.exposed.dao.id.IntIdTable
import java.util.*

object WebUserTable : IntIdTable("WebUsers") {
    val username = varchar("username", 255).primaryKey(2)
    val password = binary("password", 64)
    val salt = uuid("salt").clientDefault { UUID.randomUUID() }
}