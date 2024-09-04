package com.wenubey.routing.video

import com.wenubey.repository.VideoRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.Logger

fun Route.putLastWatched(videoRepository: VideoRepository, logger: Logger) {
    put(LAST_WATCH_ENDPOINT) {
        val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest, "Missing ID")
        val newLastWatched = call.receive<Long>()

        val isUpdated =  withContext(Dispatchers.IO) {
             videoRepository.updateLastWatched(id, newLastWatched)
        }

        if (isUpdated) {
            logger.info("Last watched time updated successfully.")
            call.respond(HttpStatusCode.OK, "Last watched time updated successfully.")
        } else {
            logger.error("Video not found")
            call.respond(HttpStatusCode.NotFound, "Video not found")
        }
    }
}

private const val LAST_WATCH_ENDPOINT = "/video/{id}/lastWatched"