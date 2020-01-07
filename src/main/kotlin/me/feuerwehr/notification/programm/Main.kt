package me.feuerwehr.notification.programm

import com.coreoz.wisp.Scheduler
import me.feuerwehr.notification.server.NotificationServer
import me.feuerwehr.notification.server.api.Loader
import java.util.concurrent.ExecutorService
import javax.inject.Inject



class Main (
    private val scheduler: Scheduler,
    private val executor: ExecutorService
): Loader {
    @Inject
    lateinit var webModule: NotificationServer

    override suspend fun enable() {
        webModule.enable()
    }
}