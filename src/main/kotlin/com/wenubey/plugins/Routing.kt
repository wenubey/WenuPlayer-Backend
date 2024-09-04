package com.wenubey.plugins

import com.wenubey.repository.VideoRepository
import com.wenubey.routing.video.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.slf4j.Logger

fun Application.videosRouting(videoRepository: VideoRepository, logger: Logger) {
    routing {
        route(VIDEOS_ROUTE) {
            postUploadVideo(videoRepository, logger)
            getAllVideoSummaries(videoRepository, logger)
            getVideoById(videoRepository, logger)
            putLastWatched(videoRepository, logger)
            softDeleteVideoById(videoRepository, logger)
            restoreVideoById(videoRepository, logger)
        }
    }
}


private const val VIDEOS_ROUTE = "/videos"
