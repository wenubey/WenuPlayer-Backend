package com.wenubey.repository

import com.mongodb.client.MongoCollection
import com.mongodb.client.gridfs.GridFSBucket
import com.wenubey.model.Video
import com.wenubey.model.VideoStream
import com.wenubey.model.VideoSummary
import com.wenubey.util.safeRun
import org.bson.types.ObjectId
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.lt
import org.litote.kmongo.setValue
import org.slf4j.LoggerFactory
import java.io.InputStream

class VideoRepositoryImpl(private val videoCollection: MongoCollection<Video>, private val gridFSBucket: GridFSBucket) : VideoRepository {

    private val logger = LoggerFactory.getLogger(VideoRepositoryImpl::class.java)

    override suspend fun uploadVideo(video: Video, fileStream: InputStream): Boolean =
        safeRun(logger = logger) {
            gridFSBucket.uploadFromStream(video.id, fileStream)
            videoCollection.insertOne(video).wasAcknowledged()
        } == true

    override suspend fun getVideoById(id: String): VideoStream? = safeRun(logger) {
        val video = videoCollection.findOne(Video::id eq id)
        val inputStream = gridFSBucket.openDownloadStream(video!!.id)
        VideoStream(video, inputStream)
    }

    override suspend fun getAllVideoSummaries(): List<VideoSummary> = safeRun(logger) {
        videoCollection.find(Video::deletedAt eq null)
            .toList()
            .map { VideoSummary(it.id, it.title) }
    } ?: emptyList()

    override suspend fun softDeleteVideoById(id: String): Boolean = safeRun(logger) {
        val updateResult = videoCollection.updateOne(
            Video::id eq id,
            setValue(Video::deletedAt, System.currentTimeMillis())
        )
        updateResult.wasAcknowledged()
    } == true

    override suspend fun restoreVideoById(id: String): Boolean = safeRun(logger) {
        val updateResult = videoCollection.updateOne(
            Video::id eq id,
            setValue(Video::deletedAt, null)
        )
        updateResult.wasAcknowledged()
    } == true

    override suspend fun permanentlyDeleteOldVideos(): Int = safeRun(logger) {
        videoCollection.find(Video::deletedAt lt SIX_HOURS_AGO).toList().forEach { video ->
            try {
                if (video.id.length == 24) {  // Check if the ID is 24 characters long
                    gridFSBucket.delete(ObjectId(video.id))
                } else {
                    logger.warn("Skipping invalid ObjectId: ${video.id}")
                }
            } catch (e: Exception) {
                logger.error("Error deleting video with ID ${video.id}: ${e.localizedMessage}", e)
            }
        }
        val deleteResult = videoCollection.deleteMany(
            Video::deletedAt lt SIX_HOURS_AGO
        )
        deleteResult.deletedCount.toInt()
    } ?: 0

    override suspend fun updateLastWatched(id: String, lastWatched: Long): Boolean = safeRun(logger){
        val updateResult = videoCollection.updateOne(Video::id eq id, setValue(Video::lastWatched, lastWatched))
        updateResult.wasAcknowledged()
    } == true


    private companion object {
        val SIX_HOURS_AGO = System.currentTimeMillis() - 6 * 60 * 60 * 1000
    }
}