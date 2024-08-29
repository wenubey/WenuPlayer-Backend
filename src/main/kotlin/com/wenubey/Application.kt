package com.wenubey


import com.wenubey.config.MongoConfig
import com.wenubey.plugins.*
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    MongoConfig.init()

    configureMonitoring()
    configureSerialization()
    configureRouting()
}
