package me.feuerwehr.notification.server

import com.sun.xml.internal.bind.v2.model.core.ID
import org.jetbrains.exposed.dao.id.EntityID
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.*
import java.net.Socket
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList

class ServerThread(client: Socket) {
    val logger = LoggerFactory.getLogger("App Server")
    private val client: Socket = client
    private val reader: Scanner = Scanner(client.getInputStream())
    private val writer: OutputStream = client.getOutputStream()
    private var running: Boolean = false

    fun run() {
        running = true

        while (running) {
            try {
                val text = reader.nextLine()
                if (text == "EXIT"){
                    shutdown()
                    continue
                }
                logger.info(text)
            } catch (ex: Exception) {
                // TODO: Implement exception handling
                shutdown()
            } finally {

            }

        }
    }

    private fun write(message: String) {
        writer.write((message + '\n').toByteArray(Charset.defaultCharset()))
    }

    private fun shutdown() {
        running = false
        client.close()
        logger.info("${client.inetAddress.hostAddress} closed the connection")
    }

}