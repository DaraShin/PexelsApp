package com.shinkevich.pexelsapp.Model.Database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Provides
    fun provideChannelDao(imagesDb: ImagesDB): ImageDao {
        return imagesDb.imageDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): ImagesDB {
        return Room.databaseBuilder(
            appContext,
            ImagesDB::class.java,
            "images_database"
        ).build()
    }
}