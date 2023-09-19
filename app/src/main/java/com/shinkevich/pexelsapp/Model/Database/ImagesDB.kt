package com.shinkevich.pexelsapp.Model.Database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ImageEntity::class], version = 2)
abstract class ImagesDB : RoomDatabase() {
    abstract fun imageDao(): ImageDao
}