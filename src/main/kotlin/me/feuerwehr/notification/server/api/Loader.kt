package me.feuerwehr.notification.server.api

import org.koin.core.KoinComponent

interface Loader : KoinComponent {
    suspend fun enable() {}
    suspend fun disable() {}
}