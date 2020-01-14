package me.feuerwehr.notification.server

import java.net.Socket

class ServerThread constructor(
    socket: Socket, sockets : ArrayList<Socket>
    ): Thread(){
    private var socket: Socket? = null
    private lateinit var sockets: ArrayList<Socket>

}