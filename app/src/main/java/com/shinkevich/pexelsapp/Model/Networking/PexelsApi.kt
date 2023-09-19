package com.shinkevich.pexelsapp.Model.Networking

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PexelsApi {
    @GET("search?per_page=10")
    suspend fun searchImages(@Query("query") query : String) : Response<ImageSearchResponse>

    @GET("curated?per_page=10")
    suspend fun getPopularImages() : Response<ImageSearchResponse>

    @GET("collections/featured?per_page=7")
    suspend fun getFeaturedCollections(): Response<FeaturedCollectionsResponse>

    @GET("photos/{id}")
    suspend fun getPhotoById(@Path("id") photoId: Long): Response<Photo>

}