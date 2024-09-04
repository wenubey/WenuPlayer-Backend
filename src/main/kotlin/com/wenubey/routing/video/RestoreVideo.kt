package com.wenubey.routing.video

import com.wenubey.repository.VideoRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.Logger

fun Route.restoreVideoById(videoRepository: VideoRepository, logger: Logger) {
    put(RESTORE_VIDEO_ENDPOINT) {
        val videoId = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest, "Missing ID")

        val isRestored = withContext(Dispatchers.IO) {
            videoRepository.restoreVideoById(videoId)
        }

        if (isRestored) {
            logger.info("Video restored successfully")
            call.respond(HttpStatusCode.OK, "Video restored successfully")
        } else {
            logger.error("Video not found.")
            call.respond(HttpStatusCode.NotFound, "Video not found.")
        }
    }
}

private const val RESTORE_VIDEO_ENDPOINT = "/video/{id}/restore"