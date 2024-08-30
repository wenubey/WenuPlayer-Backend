package com.wenubey.routing.video

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.softDeleteVideoById() {
    delete(DELETE_VIDEO_ENDPOINT) {
        val videoId = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing ID")

        // TODO create videoRepo and delete video send proper respond NoContent or NotFound
    }

}

private const val DELETE_VIDEO_ENDPOINT = "/video/{id}/trash"