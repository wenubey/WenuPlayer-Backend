package com.wenubey.routing.video

import com.wenubey.repository.VideoRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.Logger

fun Route.softDeleteVideoById(videoRepository: VideoRepository, logger: Logger) {
    delete(DELETE_VIDEO_ENDPOINT) {
        val videoId = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing ID")

        val isDeleted = withContext(Dispatchers.IO) {
            videoRepository.softDeleteVideoById(videoId)
        }

        if (isDeleted) {
            logger.info("Soft delete successfully completed.")
            call.respond(HttpStatusCode.NoContent)
        } else {
            logger.error("Video not found.")
            call.respond(HttpStatusCode.NotFound, "Video not found.")
        }
    }

}

private const val DELETE_VIDEO_ENDPOINT = "/video/{id}/trash"