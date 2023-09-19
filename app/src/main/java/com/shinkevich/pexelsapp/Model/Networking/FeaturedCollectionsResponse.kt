package com.shinkevich.pexelsapp.Model.Networking

import com.google.gson.annotations.SerializedName

data class FeaturedCollectionsResponse(
    val collections: List<Collection>,
    val page: Long,
    @SerializedName("per_page")
    val perPage: Long,
    @SerializedName("total_results")
    val totalResults: Long,
    @SerializedName("next_page")
    val nextPage: String,
    @SerializedName("prev_page")
    val prevPage: String
)

data class Collection(
    val id: String,
    val title: String,
    val description: String,
    val private: Boolean,
    @SerializedName("media_count")
    val mediaCount: Long,
    @SerializedName("photos_count")
    val photosCount: Long,
    @SerializedName("videos_count")
    val videosCount: Long
)