package com.shinkevich.pexelsapp.Model.Database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image")
data class ImageEntity(
    @PrimaryKey
    @ColumnInfo(name = "image_id")
    val imageId: Long,
    @ColumnInfo(name = "file_path")
    val filePath: String,
    val author: String
)