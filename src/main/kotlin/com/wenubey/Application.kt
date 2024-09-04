package com.wenubey



import com.wenubey.config.MongoConfig
import com.wenubey.plugins.*
import com.wenubey.repository.VideoRepository
import com.wenubey.repository.VideoRepositoryImpl
import io.ktor.server.application.*
import io.ktor.server.netty.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    MongoConfig.init()
    val videoCollection = MongoConfig.videoCollection()
    val gridFSBucket = MongoConfig.gridFSBucket()
    val logger = LoggerFactory.getLogger(Application::class.java)
    val videoRepository = VideoRepositoryImpl(videoCollection, gridFSBucket)

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

