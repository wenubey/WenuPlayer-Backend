package com.wenubey.config

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.model.Indexes
import com.wenubey.model.Video
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
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

    private val client = KMongo.createClient(settings).coroutine
    val database: CoroutineDatabase = client.getDatabase(DB_NAME)

    private val videoCollection: CoroutineCollection<Video> = database.getCollection(COLLECTION_NAME)


    fun init() {
        runBlocking {
            createIndexes()

            checkConnection()
        }
    }

    private suspend fun createIndexes() {
        try {
            videoCollection.createIndex(Indexes.ascending(TITLE_FIELD))
            logger.info("Indexes created successfully")
        } catch (e: Exception) {
            logger.error("Failed to create indexes", e)
        }
    }

    private suspend fun checkConnection() {
        try {
            val databases = client.listDatabaseNames()
            logger.info("Connected to MongoDB. Databases: $databases")
        } catch (e: Exception) {
            logger.error("Error connecting to MongoDB", e)
        }
    }
}