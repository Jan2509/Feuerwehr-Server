package me.feuerwehr.notification.server.database.table


import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import java.util.*

object WebUserTable : IntIdTable("Users") {
    val username = varchar("username", 255)
    val password = binary("password", 64)
    val salt = uuid("salt").clientDefault { UUID.randomUUID() }
    override val primaryKey = PrimaryKey(id, name = "UserID")
}