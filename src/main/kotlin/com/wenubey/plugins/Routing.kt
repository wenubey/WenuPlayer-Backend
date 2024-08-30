package com.wenubey.plugins

import com.wenubey.routing.video.getAllVideosSummaries
import com.wenubey.routing.video.getVideoById
import com.wenubey.routing.video.postUploadVideo
import com.wenubey.routing.video.putLastWatched
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.videosRouting() {
    routing {
        route(VIDEOS_ROUTE) {
            postUploadVideo()
            getAllVideosSummaries()
            getVideoById()
            putLastWatched()
        }
    }
}


private const val VIDEOS_ROUTE = "/videos"
