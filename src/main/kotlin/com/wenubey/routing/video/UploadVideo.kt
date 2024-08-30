package com.wenubey.routing.video

import com.wenubey.model.Video
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
import kotlin.io.path.createTempDirectory
import kotlin.io.path.outputStream

fun Route.postUploadVideo() {
    post(UPLOAD_ENDPOINT) {
        val multipart = call.receiveMultipart()
        var filePart: PartData.FileItem? = null
        var uuid: String? = null
        var fileSize: Long = 0

        multipart.forEachPart { part ->
            when (part) {
                is PartData.FileItem -> {
                    filePart = part
                    fileSize = part.streamProvider().available().toLong()
                }

                is PartData.FormItem -> {
                    if (part.name == UUID_FIELD) {
                        uuid = part.value
                    }
                }

                else -> Unit
            }
            part.dispose()
        }

        if (uuid == null) return@post call.respond(HttpStatusCode.BadRequest, "UUID not provided")
        val file = filePart ?: return@post call.respond(HttpStatusCode.BadRequest, "No file uploaded.")
        val contentType = file.contentType!!
        val fileName = file.originalFileName ?: "unknown"

        if (!isValidFileType(contentType)) return@post call.respond(
            HttpStatusCode.UnsupportedMediaType,
            "Invalid file type."
        )

        if (!isFileSizeValid(fileSize, MAX_SIZE)) return@post call.respond(
            HttpStatusCode.PayloadTooLarge,
            "File size exceeds limit."
        )
        val tempDir = createTempDirectory()
        val filePath = tempDir.resolve(fileName)
        file.streamProvider().use { input ->
            filePath.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        val video = Video(
            id = uuid!!,
            title = fileName,
            url = filePath.toString(),
            lastWatched = 0L
        )

        withContext(Dispatchers.IO) {
           // TODO create video repo and add video videoRepo.addVideo(video)
        }

        call.respond(HttpStatusCode.Created, "Video uploaded successfully with UUID: $uuid")
    }
}

private const val UPLOAD_ENDPOINT = "/upload"
private const val UUID_FIELD = "uuid"
private const val MAX_SIZE = 1024 * 1024 * 1024L // 1GB