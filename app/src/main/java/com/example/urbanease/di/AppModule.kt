package com.example.urbanease.di

import com.example.urbanease.repository.HouseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideHouseRepository(): HouseRepository {
        return HouseRepository()
    }
}