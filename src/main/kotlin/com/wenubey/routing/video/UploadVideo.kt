package com.wenubey.routing.video

import com.wenubey.model.Video
import com.wenubey.repository.VideoRepository
import com.wenubey.routing.isFileSizeValid
import com.wenubey.routing.isValidFileType
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createTempDirectory

fun Route.postUploadVideo(videoRepository: VideoRepository, logger: Logger) {
    post(UPLOAD_ENDPOINT) {
        val multipart = call.receiveMultipart()
        var filePart: PartData.FileItem? = null
        var uuid: String? = null
        var fileSize: Long = 0
        var tempFilePath: Path? = null

        multipart.forEachPart { part ->
            when (part) {
                is PartData.FileItem -> {
                    filePart = part

                    tempFilePath = createTempDirectory().resolve(part.originalFileName ?: "tempFile")
                    part.streamProvider().use {
                        Files.copy(it, tempFilePath)
                    }

                    fileSize = Files.size(tempFilePath)
                    logger.info("fileSize: $fileSize")
                }

                is PartData.FormItem -> {
                    if (part.name == UUID_FIELD) {
                        uuid = part.value
                    }
                }

                else -> Unit
            }
            logger.info("fileSize: $fileSize")
            part.dispose()
        }

        if (uuid == null) return@post call.respond(HttpStatusCode.BadRequest, "UUID not provided")
        val file = filePart ?: return@post call.respond(HttpStatusCode.BadRequest, "No file uploaded.")
        val contentType = file.contentType!!
        val fileName = file.originalFileName ?: "unknown"
        if (!isValidFileType(contentType)) return@post call.respond(
            HttpStatusCode.UnsupportedMediaType,
            "Invalid file type. $contentType"
        )

        if (!isFileSizeValid(fileSize, MAX_SIZE)) return@post call.respond(
            HttpStatusCode.PayloadTooLarge,
            "File size exceeds limit."
        )

        val video = Video(
            id = uuid,
            title = fileName,
            url = fileName,
            lastWatched = 0L
        )
        val isUploaded =  withContext(Dispatchers.IO) {
            tempFilePath?.let { path ->
                videoRepository.uploadVideo(video, Files.newInputStream(path))
            } == true
        }


        tempFilePath?.let { Files.deleteIfExists(it) }

        if (isUploaded) {
            call.respond(HttpStatusCode.Created, "Video uploaded successfully with UUID: $uuid")
        }else {
            call.respond(HttpStatusCode.InternalServerError, "Failed to upload video.")
        }

    }
}

private const val UPLOAD_ENDPOINT = "/upload"
private const val UUID_FIELD = "uuid"
private const val MAX_SIZE = 1024 * 1024 * 1024L // 1GB