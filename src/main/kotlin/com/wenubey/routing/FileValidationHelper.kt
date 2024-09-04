package com.wenubey.routing

import io.ktor.http.*

fun isValidFileType(contentType: ContentType): Boolean {
    val validSubtypes = setOf("mp4", "x-matroska")
    return contentType.contentType == "video" && contentType.contentSubtype in validSubtypes
}

fun isFileSizeValid(size: Long, maxSize: Long): Boolean = size <= maxSize