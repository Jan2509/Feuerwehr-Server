package me.feuerwehr.notification.server.web.components.html

import com.uchuhimo.konf.Config
import io.ktor.html.Template
import kotlinx.html.*
import me.feuerwehr.notification.server.ConfigWrapper
import me.feuerwehr.notification.server.ConnectionSpec
import me.feuerwehr.notification.server.DatabaseSpec
import me.feuerwehr.notification.server.Konf
import me.feuerwehr.notification.server.database.dao.WebEinsatzDAO
import me.feuerwehr.notification.server.database.dao.WebUserDAO
import me.feuerwehr.notification.server.database.table.WebEinsatzTable
import me.feuerwehr.notification.server.database.table.WebUserTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

object EinsatzList : Template<DIV> {
    override fun DIV.apply() {
        val config = initConfig()
        val database = createDatabase(config)
        div(classes = "table-container") {
            table(classes = "table is-bordered is-striped is-narrow is-hoverable is-fullwidth") {
                id = "example"
                thead {
                    tr {
                        th { +"ID" }
                        th { +"Stichwort" }
                        th { +"Strasse" }
                        th { +"HausNr" }
                        th { +"PLZ" }
                        th { +"Ort" }
                        th { +"Datum" }
                    }
                }
                tbody {
                    var it = 1
                    do {
                        val einsatz = transaction(database) {
                            WebEinsatzDAO.find { WebEinsatzTable.id eq it }.firstOrNull()
                        }
                        if (einsatz != null) {
                            tr {
                                td { +einsatz.id.toString() }
                                td { +einsatz.stichwort }
                                td { +einsatz.strasse }
                                td { +einsatz.hausnr }
                                td { +einsatz.plz }
                                td { +einsatz.ort }
                                td { +einsatz.datum.toString() }
                            }
                            it++
                        } else if (einsatz == null && it <2) {
                            tr {
                                td{+"Keine"}
                            }
                        }
                    } while (einsatz != null)
                }
            }
        }
    }
}
private val configFile: File = File("config.yml")
private fun initConfig(): Konf = ConfigWrapper(Config {
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