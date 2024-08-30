package com.wenubey.routing.video

import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Route.getAllVideosSummaries() {
    get(GET_ALL_VIDEOS_SUMMARIES_ENDPOINT) {
        val videos = withContext(Dispatchers.IO) {
            // TODO create video repository and fetch videos summaries
        }
    }
}

private const val GET_ALL_VIDEOS_SUMMARIES_ENDPOINT = "/videos"