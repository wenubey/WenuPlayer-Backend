package com.wenubey.plugins

import com.wenubey.routing.video.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.videosRouting() {
    routing {
        route(VIDEOS_ROUTE) {
            postUploadVideo()
            getAllVideosSummaries()
            getVideoById()
            putLastWatched()
            softDeleteVideoById()
            restoreVideoById()
        }
    }
}


private const val VIDEOS_ROUTE = "/videos"
