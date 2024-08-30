package com.wenubey.routing.video

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.restoreVideoById() {
    put(RESTORE_VIDEO_ENDPOINT) {
        val videoId = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest, "Missing ID")

        // TODO create video repo and call restoreVideoById
    }
}

private const val RESTORE_VIDEO_ENDPOINT = "/video/{id}/restore"