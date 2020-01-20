package me.feuerwehr.notification.server.database.dao

import me.feuerwehr.notification.server.database.table.WebUserTable
import com.google.common.hash.Hashing
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import java.nio.charset.StandardCharsets
import java.util.*

class WebUserDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object Static : IntEntityClass<WebUserDAO>(
        WebUserTable
    ) {
        private fun hashPassword(password: String, salt: UUID) =
            Hashing.sha512().hashString(password + salt, StandardCharsets.UTF_8).asBytes()
    }
    var username by WebUserTable.username
    var vorname by WebUserTable.vorname
    private var passwordHash by WebUserTable.password
    var tel by WebUserTable.tel
    var geb by WebUserTable.geb
    var ein by WebUserTable.ein
    private var salt by WebUserTable.salt


    fun hasPassword(password: String) = passwordHash.contentEquals(
        hashPassword(
            password,
            salt
        )
    )

    fun setPassword(password: String) {
        salt = UUID.randomUUID()
        passwordHash = hashPassword(password, salt)
    }

}