package me.feuerwehr.notification.server.web.components.html

import com.uchuhimo.konf.Config
import io.ktor.html.Template
import kotlinx.html.*
import me.feuerwehr.notification.server.ConfigWrapper
import me.feuerwehr.notification.server.ConnectionSpec
import me.feuerwehr.notification.server.DatabaseSpec
import me.feuerwehr.notification.server.Konf
import me.feuerwehr.notification.server.database.dao.AusgebildetDAO
import me.feuerwehr.notification.server.database.dao.WebAusbildungsDAO
import me.feuerwehr.notification.server.database.table.AusgebildetTable
import me.feuerwehr.notification.server.database.table.EinsatzTeilnahmeTable
import me.feuerwehr.notification.server.database.table.WebAusbildungsTable
import me.feuerwehr.notification.server.database.table.WebEinsatzTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

object AlarmResponse : Template<BODY> {
    override fun BODY.apply() {

        val logger: Logger = LoggerFactory.getLogger("WebServer")
        val config = initConfig()
        val database = createDatabase(config)
        div(classes = "table-container") {
            val voralarm = transaction(database) {
                WebEinsatzTable.slice(
                    WebEinsatzTable.id,
                    WebEinsatzTable.stichwort,
                    WebEinsatzTable.strasse,
                    WebEinsatzTable.hausnr,
                    WebEinsatzTable.plz,
                    WebEinsatzTable.ort,
                    WebEinsatzTable.datum,
                    WebEinsatzTable.zeit,
                    WebEinsatzTable.bemerkungen
                ).selectAll()
                    .orderBy(WebEinsatzTable.id, SortOrder.DESC).firstOrNull()
            }
            table(classes = "table is-bordered is-striped is-narrow is-hoverable is-fullwidth") {
                tr {
                    th { +"ID" }
                    th { +"Stichwort" }
                    th { +"Strasse" }
                    th { +"Haus Nr" }
                    th { +"PLZ" }
                    th { +"Ort" }
                    th { +"Datum" }
                    th { +"Zeit" }
                    th { +"Bemerkungen" }
                }
                    if (voralarm != null) {
                        tr{
                            th { +voralarm[WebEinsatzTable.id].toString() }
                            th { +voralarm[WebEinsatzTable.stichwort] }
                            th { +voralarm[WebEinsatzTable.strasse] }
                            th { +voralarm[WebEinsatzTable.hausnr] }
                            th { +voralarm[WebEinsatzTable.plz] }
                            th { +voralarm[WebEinsatzTable.ort] }
                            th { +voralarm[WebEinsatzTable.datum] }
                            th { +voralarm[WebEinsatzTable.zeit] }
                            th { +voralarm[WebEinsatzTable.bemerkungen] }
                    }
                } else {
                        tr{
                            th {+ "Keiner"}
                        }
                    }
                table(classes = "table is-bordered is-striped is-narrow is-hoverable is-fullwidth") {
                    id = "example"
                    thead {
                        tr {
                            th { +"Zugesagt" }
                            th { +"Abgesagt" }
                            th { +"Maschinisten" }
                            th { +"Gruppenfuehrer" }
                            th { +"Truppfuehrer" }
                            th { +"Truppmann" }
                            th { +"AGT-Traeger" }
                        }
                    }
                    tbody {
                        val voralarm = transaction(database) {
                            WebEinsatzTable.slice(WebEinsatzTable.id).selectAll()
                                .orderBy(WebEinsatzTable.id, SortOrder.DESC).firstOrNull()
                        }
                        if (voralarm != null) {
                            var zugesagt = 0
                            var abgesagt = 0
                            var maschinist = 0
                            var gruppenfuehrer = 0
                            var truppfuehrer = 0
                            var truppmann = 0
                            var agt = 0
                            var sonstige = 0
                            val teilgenommen = transaction(database) {
                                EinsatzTeilnahmeTable.slice(
                                    EinsatzTeilnahmeTable.EID,
                                    EinsatzTeilnahmeTable.MID,
                                    EinsatzTeilnahmeTable.teilgenommen
                                ).select(
                                    EinsatzTeilnahmeTable.EID eq voralarm[WebEinsatzTable.id]
                                )
                                    .toList()
                            }
                            teilgenommen.forEach { t ->
                                val mid = t[EinsatzTeilnahmeTable.MID]
                                val zusage = t[EinsatzTeilnahmeTable.teilgenommen]
                                if (zusage) {
                                    zugesagt++
                                    val hatausbildung =
                                        transaction { AusgebildetDAO.find { AusgebildetTable.MID eq mid } }
                                    logger.info(hatausbildung.toString())
                                    if (hatausbildung != null) {
                                        var ausgebildet = transaction {
                                            AusgebildetTable.slice(
                                                AusgebildetTable.MID,
                                                AusgebildetTable.AID
                                            )
                                                .selectAll().having {
                                                    AusgebildetTable.MID eq mid
                                                }.toList()
                                        }
                                        ausgebildet.forEach {
                                            var ausbildung =
                                                transaction(database) { WebAusbildungsDAO.find { WebAusbildungsTable.id eq it[AusgebildetTable.AID] } }.firstOrNull()
                                            if (ausbildung!!.bezeichnung == "gruppenfuehrer") {
                                                gruppenfuehrer++
                                            } else if (ausbildung!!.bezeichnung == "truppfuehrer") {
                                                truppfuehrer++
                                            } else if (ausbildung!!.bezeichnung == "truppmann") {
                                                truppmann++
                                            } else if (ausbildung!!.bezeichnung == "agt") {
                                                agt++
                                            } else if (ausbildung!!.bezeichnung == "maschinist") {
                                                maschinist++
                                            }
                                        }
                                    }
                                } else {
                                    abgesagt++
                                }
                            }
                            tr {
                                th { +zugesagt.toString() }
                                th { +abgesagt.toString() }
                                th { +maschinist.toString() }
                                th { +gruppenfuehrer.toString() }
                                th { +truppfuehrer.toString() }
                                th { +truppmann.toString() }
                                th { +agt.toString() }
                            }
                        } else {
                            tr {
                                td { +"Keiner" }
                            }
                        }
                    }
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