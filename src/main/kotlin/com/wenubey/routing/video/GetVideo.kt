package com.wenubey.routing.video

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Route.getVideoById() {
    get(GET_VIDEO_ENDPOINT) {
        val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing ID")

        val video = withContext(Dispatchers.IO) {
            // TODO create videoRepository and send via OK
        }
    }
}

private const val GET_VIDEO_ENDPOINT = "/video/{id}"