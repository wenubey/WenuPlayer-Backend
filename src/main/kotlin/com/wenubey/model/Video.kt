package com.wenubey.model

import kotlinx.serialization.Serializable

@Serializable
data class Video(
    val id: String,
    val title: String,
    val url: String,
    val lastWatched: Long
)
