package com.wenubey.config

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.gridfs.GridFSBucket
import com.mongodb.client.gridfs.GridFSBuckets
import com.mongodb.client.model.Indexes
import com.wenubey.model.Video
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.KMongo
import org.slf4j.LoggerFactory

object MongoConfig {
    private const val DB_NAME = "wenuplayer_database"
    private const val COLLECTION_NAME = "videos"
    private const val TITLE_FIELD = "title"

    private val logger = LoggerFactory.getLogger(MongoConfig::class.java)
    private val connectionString = ConnectionString("mongodb://localhost:27017")
    private val settings = MongoClientSettings.builder()
        .applyConnectionString(connectionString)
        .retryWrites(true)
        .build()

    private val client = KMongo.createClient(settings)
    val database = client.getDatabase(DB_NAME)

    private val videoCollection = database.getCollection(COLLECTION_NAME, Video::class.java)
    val gridFSBucket: GridFSBucket = GridFSBuckets.create(database, COLLECTION_NAME)

    fun init() =
        runBlocking {
            createIndexes()

            checkConnection()
        }

    fun videoCollection() = videoCollection
    fun gridFSBucket() = gridFSBucket

    private fun createIndexes() {
        try {
            videoCollection.createIndex(Indexes.ascending(TITLE_FIELD))
            logger.info("Indexes created successfully")
        } catch (e: Exception) {
            logger.error("Failed to create indexes", e)
        }
    }

    private fun checkConnection() {
        try {
            val databases = client.listDatabaseNames()
            logger.info("Connected to MongoDB. Databases: $databases")
        } catch (e: Exception) {
            logger.error("Error connecting to MongoDB", e)
        }
    }
}