package com.wenubey.model


import java.io.InputStream

data class VideoStream(
    val video: Video,
    val inputStream: InputStream,
)
