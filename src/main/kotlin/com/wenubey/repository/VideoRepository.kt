package com.wenubey.repository

import com.wenubey.model.Video
import com.wenubey.model.VideoStream
import com.wenubey.model.VideoSummary
import java.io.InputStream

interface VideoRepository {
    suspend fun uploadVideo(video: Video, fileStream: InputStream): Boolean
    suspend fun getVideoById(id: String): VideoStream?
    suspend fun getAllVideoSummaries(): List<VideoSummary>
    suspend fun softDeleteVideoById(id: String): Boolean
    suspend fun restoreVideoById(id: String): Boolean
    suspend fun permanentlyDeleteOldVideos(): Int
    suspend fun updateLastWatched(id: String, lastWatched: Long): Boolean
}