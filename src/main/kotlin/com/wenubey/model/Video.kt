package com.wenubey.model

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId


@Serializable
data class Video(
    @BsonId
    val id: String,
    val title: String,
    val url: String,
    val lastWatched: Long,
    val deletedAt: Long? = null,
    val fileObjectId: String? = null
)
