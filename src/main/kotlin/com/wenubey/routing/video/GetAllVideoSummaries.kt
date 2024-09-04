package com.wenubey.routing.video

import com.wenubey.repository.VideoRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.Logger

fun Route.getAllVideoSummaries(videoRepository: VideoRepository, logger: Logger) {
    get(GET_ALL_VIDEOS_SUMMARIES_ENDPOINT) {
        val videoSummaries = withContext(Dispatchers.IO) {
            videoRepository.getAllVideoSummaries()
        }
        if (videoSummaries.isNotEmpty()) {
            logger.info("video summaries found: ${videoSummaries.first().title}")
            call.respond(HttpStatusCode.OK, videoSummaries)
        } else {
            logger.error("Couldn't fetch video summaries.")
            call.respond(HttpStatusCode.InternalServerError, "Couldn't fetch video summaries.")
        }
    }
}

private const val GET_ALL_VIDEOS_SUMMARIES_ENDPOINT = "/video-summaries"