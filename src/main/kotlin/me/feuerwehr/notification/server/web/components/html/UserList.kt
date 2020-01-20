package me.feuerwehr.notification.server.web.components.html

import io.ktor.html.Template
import kotlinx.html.*
import me.feuerwehr.notification.server.ConfigWrapper
import me.feuerwehr.notification.server.ConnectionSpec
import me.feuerwehr.notification.server.DatabaseSpec
import me.feuerwehr.notification.server.Konf
import me.feuerwehr.notification.server.database.dao.WebUserDAO
import me.feuerwehr.notification.server.database.table.WebUserTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.max
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File


object UserList : Template<DIV> {
    override fun DIV.apply() {
        val config = initConfig()
        val database = createDatabase(config)
        div(classes = "table-container") {
            table(classes = "table is-bordered is-striped is-narrow is-hoverable is-fullwidth") {
                id = "example"
                thead {
                    tr {
                        th { +"ID" }
                        th { +"Name" }
                        th { +"Vorname" }
                        th { +"Tel" }
                        th { +"Geburtsdatum" }
                        th { +"Eintrittsdatum" }
                    }
                }
                tbody {
                    var it = 1
                    do {
                        val user = transaction(database) {
                            WebUserDAO.find { WebUserTable.id eq it }.firstOrNull()
                        }
                        if (user != null) {
                            tr {
                                td { +user.id.toString() }
                                td { +user.username }
                                td { +user.vorname }
                                td { +user.tel }
                                td { +user.geb.toString() }
                                td { +user.ein.toString() }
                            }
                            it++
                        }
                    } while (user != null)
                }
            }
        }
    }
}
private val configFile: File = File("config.yml")
private fun initConfig(): Konf = ConfigWrapper(Konf {
    listOf(
        ConnectionSpec,
        DatabaseSpec
    ).forEach { spec ->
        addSpec(spec)
    }
}).also { config ->
    if (configFile.exists())
        config.load(configFile)
    else {
        configFile.parentFile?.mkdirs()
        config.save(configFile)
    }
}
private fun createDatabase(config: Konf) = Database.connect(
    "jdbc:mysql://" +
            config[DatabaseSpec.dataHost] +
            ":" + config[DatabaseSpec.dataPort] + "/" + config[DatabaseSpec.database],
    driver = "com.mysql.jdbc.Driver",
    user = config[DatabaseSpec.dataUser],
    password = config[DatabaseSpec.dataPass]
)