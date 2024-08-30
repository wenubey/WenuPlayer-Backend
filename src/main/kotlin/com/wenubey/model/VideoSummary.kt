package com.wenubey.model

import kotlinx.serialization.Serializable

@Serializable
data class VideoSummary(
    val id: String,
    val title: String
)
