package com.shinkevich.pexelsapp.Model

data class Image(
    val id: Long,
    val url: String,
    val author: String,
    val filePath: String = ""
)