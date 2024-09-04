package com.wenubey.routing.video

import com.wenubey.repository.VideoRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import java.io.File
import java.nio.file.Files

fun Route.getVideoById(videoRepository: VideoRepository, logger: Logger) {
    get(GET_VIDEO_ENDPOINT) {
        val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing ID")

        val videoMetadata = withContext(Dispatchers.IO) {
            videoRepository.getVideoById(id)
        }
        if (videoMetadata != null) {
            call.respond(HttpStatusCode.OK, videoMetadata.video)
        } else {
            call.respond(HttpStatusCode.NotFound, "Video not found")
        }
    }

    get(GET_VIDEO_STREAM_ENDPOINT) {
        val videoId = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing ID")
        logger.info("Received request to get video with ID: $videoId")

        val videoMetadata = withContext(Dispatchers.IO) {
            videoRepository.getVideoById(videoId)
        }

        if (videoMetadata != null) {
            val tempDir = Files.createTempDirectory("video-").toFile()
            tempDir.deleteOnExit()
            val tempFile = File(tempDir, videoMetadata.video.title)
            tempFile.deleteOnExit()

            val inputStream = videoMetadata.inputStream

            try {
                inputStream.reset()
                withContext(Dispatchers.IO) {
                    inputStream.use { stream ->
                        Files.copy(stream, tempFile.toPath())
                    }
                }
                val fileSize = tempFile.length()

                if (fileSize > 0) {
                    logger.info("Responding with file at path: ${tempFile.absolutePath}")
                    call.respondFile(tempFile)
                } else {
                    logger.error("Error: File size is 0 bytes after copy")
                    call.respond(HttpStatusCode.InternalServerError, "Video file is empty")
                }
            } catch (e: Exception) {
                logger.info("Error while copying the file: ${e.message}")
                call.respond(HttpStatusCode.InternalServerError, "Error while processing the file")
            }
        } else {
            logger.error("Video not found")
            call.respond(HttpStatusCode.NotFound, "Video not found")
        }
    }
}

private const val GET_VIDEO_ENDPOINT = "/video/{id}"
private const val GET_VIDEO_STREAM_ENDPOINT = "/video/{id}/stream"