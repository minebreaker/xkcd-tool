package rip.deadcode.xkcdtool.server

import org.eclipse.jetty.server.HttpConfiguration
import org.eclipse.jetty.server.Server


object Main {
    @JvmStatic
    fun main(args: Array<String>) {

        val config = HttpConfiguration().also {
            it.sendServerVersion = false
        }

        val port = args.firstOrNull()?.toIntOrNull() ?: 8080
        Server(port)
            .also { it.handler = Handler() }
            .start()
    }
}
