package me.feuerwehr.notification.programm


import io.ktor.util.KtorExperimentalAPI
import org.fusesource.jansi.AnsiConsole
import me.feuerwehr.notification.server.NotificationServer
import picocli.CommandLine
import javax.inject.Inject

@CommandLine.Command(
    name = "FFServer.jar",
    subcommands = [
    ]
)

class Server  : Runnable {
    @CommandLine.Option(
        names = ["--local", "-h2"]
    )
    val server = NotificationServer()
    @KtorExperimentalAPI
    override fun run(){
        try{
            server.enable()
        } catch (e:Exception) {
            e.printStackTrace()
        }
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