package com.wenubey.routing

import io.ktor.http.*

fun isValidFileType(contentType: ContentType): Boolean {
    return contentType.contentType == "video" && (contentType.contentSubtype == "mp4" || contentType.contentSubtype == "mkv")
}

fun isFileSizeValid(size: Long, maxSize: Long): Boolean = size <= maxSize