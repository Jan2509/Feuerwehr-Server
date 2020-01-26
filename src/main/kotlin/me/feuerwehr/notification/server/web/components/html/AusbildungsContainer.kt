package me.feuerwehr.notification.server.web.components.html

import com.uchuhimo.konf.Config
import io.ktor.html.Template
import kotlinx.html.*
import me.feuerwehr.notification.server.ConfigWrapper
import me.feuerwehr.notification.server.ConnectionSpec
import me.feuerwehr.notification.server.DatabaseSpec
import me.feuerwehr.notification.server.Konf
import me.feuerwehr.notification.server.database.dao.WebUserDAO
import me.feuerwehr.notification.server.database.table.WebAusbildungsTable
import me.feuerwehr.notification.server.database.table.WebUserTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

object AusbildungsContainer : Template<DIV> {
        override fun DIV.apply() {
            val config = initConfig()
            val database = createDatabase(config)
            form {
                id = "create-form"
                div("field") {
                    label("label") { +"Nachname" }
                    div("select is-fullwidth") {
                        select{
                            id = "create-name-input"
                            transaction(database) {
                                WebUserTable.slice(WebUserTable.id, WebUserTable.username).selectAll().toList()
                                    .forEach { user ->
                                        if (user[WebUserTable.username] != "Admin") {
                                            option { +user[WebUserTable.username].toString() }
                                        }
                                    }
                            }
                        }
                    }
                }
                div("field") {
                    label("label") { +"Ausbildung" }
                    div("select is-fullwidth") {
                        select{
                            id = "create-aus-input"
                            transaction(database) {
                                WebAusbildungsTable.slice(WebAusbildungsTable.id, WebAusbildungsTable.bezeichnung).selectAll().toList()
                                    .forEach { ausbildung ->
                                            option { +ausbildung[WebAusbildungsTable.bezeichnung].toString() }
                                        }
                                    }
                            }
                        }
                    }
                div("control") {
                    button(classes = "button is-success is-light is-fullwidth") {
                        id = "create-btn"
                        type = ButtonType.submit
                        + "Ausbildung Hinzufügen"

                    }
                }
            }
            script { +"addPage()" }
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