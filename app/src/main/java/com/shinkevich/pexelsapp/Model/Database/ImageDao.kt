package com.shinkevich.pexelsapp.Model.Database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ImageDao {
    @Insert
    fun insert(image: ImageEntity)

    @Query("select * from image where image_id = :imageId")
    fun getImageById(imageId: Long): ImageEntity

    @Query("select * from image")
    fun getAll(): List<ImageEntity>
}