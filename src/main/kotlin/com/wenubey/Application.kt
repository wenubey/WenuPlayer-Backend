package com.wenubey



import com.wenubey.di.loggerModule
import com.wenubey.di.mongoModule
import com.wenubey.di.repositoryModule
import com.wenubey.plugins.*
import com.wenubey.repository.VideoRepository
import io.ktor.server.application.*
import io.ktor.server.netty.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.core.context.startKoin
import org.koin.ktor.ext.inject
import org.slf4j.Logger
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {

    startKoin {
        modules(loggerModule(), mongoModule(), repositoryModule())
    }
    val logger by inject<Logger>()
    val videoRepository by inject<VideoRepository>()

    configureMonitoring()
    configureSerialization()
    videosRouting(videoRepository, logger)

    schedulePermanentDeletion(videoRepository, logger)
}

private fun Application.schedulePermanentDeletion(videoRepository: VideoRepository, logger: Logger) {
    val scope = CoroutineScope(Dispatchers.IO)

    scope.launch {
        while (isActive) {
            try {
                val deletedCount = videoRepository.permanentlyDeleteOldVideos()
                if (deletedCount > 0) {
                    logger.info("Permanently deleted $deletedCount old videos.")
                }
            } catch (e: Exception) {
                logger.error("Failed to permanently delete old videos", e)
            }

            delay(TimeUnit.HOURS.toMillis(1))
        }
    }

    environment.monitor.subscribe(ApplicationStopping) {
        scope.cancel()
    }
}

