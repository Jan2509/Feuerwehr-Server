package me.feuerwehr.notification.programm


import io.ktor.util.KtorExperimentalAPI
import me.feuerwehr.notification.server.NotificationServer
import org.slf4j.LoggerFactory
import picocli.CommandLine
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.util.ArrayList

@CommandLine.Command(
    name = "FFServer.jar",
    subcommands = [
    ]
)

class Server  : Runnable {
    @CommandLine.Option(
        names = ["--local", "-h2"]
    )
    //val echoServer = EchoServer()
    val logger = LoggerFactory.getLogger("Server")
    val server = NotificationServer()
    @KtorExperimentalAPI
    override fun run(){
        try{
            logger.info("FFServer Startet")
            server.enable()
        } catch (e:Exception) {
            e.printStackTrace()
        }
        @Throws(IOException::class)
        while(true) {
            Thread.sleep(Long.MAX_VALUE)
        }
    }
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val commandLine = CommandLine(Server::class.java)
            commandLine.execute(*args)
        }
    }
}