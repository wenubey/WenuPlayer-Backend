package com.wenubey.routing.video

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Route.putLastWatched() {
    put(LAST_WATCH_ENDPOINT) {
        val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest, "Missing ID")
        val newLastWatched = call.receive<Long>()

        withContext(Dispatchers.IO) {
            // TODO create repository and get video copy the video add newLastWatched
        }
    }
}

private const val LAST_WATCH_ENDPOINT = "/video/{id}/lastWatched"