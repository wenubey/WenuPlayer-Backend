package com.wenubey.di

import org.koin.dsl.module
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun loggerModule() = module {
        single<Logger> { LoggerFactory.getLogger(LOGGER_NAME) }
    }


const val LOGGER_NAME = "wenuplayer_logger"