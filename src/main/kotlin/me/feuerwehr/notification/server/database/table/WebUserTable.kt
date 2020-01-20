package me.feuerwehr.notification.server.database.table


import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.date
import org.jetbrains.exposed.sql.`java-time`.datetime
import java.util.*

object WebUserTable : IntIdTable("Users") {
    val username = varchar("username", 255)
    val vorname = varchar("vorname", 255)
    val password = binary("password", 64)
    val tel = varchar("tel", 255)
    val geb = varchar("geb", 100)
    val ein = varchar("ein",100)
    val salt = uuid("salt").clientDefault { UUID.randomUUID() }
    override val primaryKey = PrimaryKey(id, name = "UserID")
}