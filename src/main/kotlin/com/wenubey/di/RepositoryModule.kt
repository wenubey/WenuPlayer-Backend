package com.wenubey.di

import com.wenubey.repository.VideoRepository
import com.wenubey.repository.VideoRepositoryImpl
import org.koin.dsl.module

fun repositoryModule() = module {

    single<VideoRepository> { VideoRepositoryImpl(get(), get())}
}