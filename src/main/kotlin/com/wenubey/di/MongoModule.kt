package com.wenubey.di

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.gridfs.GridFSBucket
import com.mongodb.client.gridfs.GridFSBuckets
import com.wenubey.model.Video
import org.koin.dsl.module
import org.litote.kmongo.KMongo

fun mongoModule() = module {
    single<MongoClientSettings> {
        MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .retryWrites(true)
            .build()
    }
    single<MongoDatabase> {
        KMongo.createClient(get<MongoClientSettings>()).getDatabase(DB_NAME)
    }
    single<MongoCollection<Video>> {
        get<MongoDatabase>().getCollection(COLLECTION_NAME, Video::class.java)
    }

    single<GridFSBucket> { GridFSBuckets.create(get(), COLLECTION_NAME) }
}



const val DB_NAME = "wenuplayer_database"
const val COLLECTION_NAME = "videos"
val connectionString = ConnectionString("mongodb://localhost:27017")