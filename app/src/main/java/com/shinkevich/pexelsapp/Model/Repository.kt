package com.shinkevich.pexelsapp.Model

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import com.shinkevich.pexelsapp.Model.Database.ImageDao
import com.shinkevich.pexelsapp.Model.Database.ImageEntity
import com.shinkevich.pexelsapp.Model.Networking.NoConnectionException
import com.shinkevich.pexelsapp.Model.Networking.PexelsApi
import com.shinkevich.pexelsapp.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject


class Repository @Inject constructor(
    private val pexelsApi: PexelsApi,
    @ApplicationContext val context: Context,
    private val imageDao: ImageDao,
    private val okHttpClient: OkHttpClient
) {

    fun searchImages(searchQuery: String): Flow<RequestResult<List<Image>>> {
        return flow {
            val response = pexelsApi.searchImages(searchQuery)
            if (response.isSuccessful) {
                val imageSearchResult = response.body()!!
                val resultImageList = mutableListOf<Image>()
                for (photo in imageSearchResult.photos) {
                    resultImageList.add(Image(photo.id, photo.src.original, photo.photographer))
                }
                emit(RequestResult.Success(resultImageList as List<Image>))
            } else {
                emit(RequestResult.Failure(context.getString(R.string.error_message)))
            }
        }.catch { exception ->
            if (exception is NoConnectionException) {
                emit(RequestResult.NoInternetConnectionFailure())
            } else {
                emit(RequestResult.Failure(context.getString(R.string.error_message)))
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getPopularImages(): Flow<RequestResult<List<Image>>> {
        return flow {
            val response = pexelsApi.getPopularImages()
            if (response.isSuccessful) {
                val imageSearchResponse = response.body()!!
                val popularImageList = mutableListOf<Image>()
                for (photo in imageSearchResponse.photos) {
                    popularImageList.add(Image(photo.id, photo.src.original, photo.photographer))
                }
                emit(RequestResult.Success(popularImageList as List<Image>))
            } else {
                emit(RequestResult.Failure(context.getString(R.string.error_message)))
            }
        }.catch { exception ->
            if (exception is NoConnectionException) {
                emit(RequestResult.NoInternetConnectionFailure())
            } else {
                emit(RequestResult.Failure(context.getString(R.string.error_message)))
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getFeaturedCollections(): Flow<RequestResult<List<String>>> {
        return flow {
            val response = pexelsApi.getFeaturedCollections()
            if (response.isSuccessful) {
                val featuredCollectionsResponse = response.body()!!
                val featuredCollections = mutableListOf<String>()
                for (collection in featuredCollectionsResponse.collections) {
                    featuredCollections.add(collection.title)
                }
                emit(RequestResult.Success(featuredCollections as List<String>))
            } else {
                emit(RequestResult.Failure(context.getString(R.string.error_message)))
            }
        }.catch { exception ->
            if (exception is NoConnectionException) {
                emit(RequestResult.NoInternetConnectionFailure())
            } else {
                emit(RequestResult.Failure(context.getString(R.string.error_message)))
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getPhotoById(photoId: Long): Flow<RequestResult<Image>> {
        return flow {
            val response = pexelsApi.getPhotoById(photoId)
            if (response.isSuccessful) {
                val photo = response.body()!!
                emit(RequestResult.Success(Image(photo.id, photo.src.original, photo.photographer)))
            } else {
                emit(RequestResult.Failure(context.getString(R.string.error_message)))
            }
        }.catch { exception ->
            if (exception is NoConnectionException) {
                emit(RequestResult.NoInternetConnectionFailure())
            } else {
                emit(RequestResult.Failure(context.getString(R.string.error_message)))
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getBookmarkById(imageId: Long): Flow<ImageEntity> {
        return flow {
            emit(imageDao.getImageById(imageId))
        }.flowOn(Dispatchers.IO)
    }

    fun downloadPhoto(image: Image) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri: Uri = Uri.parse(image.url)
        val request = DownloadManager.Request(uri)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        downloadManager.enqueue(request)
    }

    fun saveImageToBookmarks(image: Image) {
        val bookmark = imageDao.getImageById(image.id)
        if(bookmark != null){
            return
        }
        val imageFilePath = saveImageFile(image)
        if (imageFilePath.isNotEmpty()) {
            val imageEntity = ImageEntity(image.id, imageFilePath, image.author)
            imageDao.insert(imageEntity)
        }
    }

    private fun saveImageFile(image: Image): String {
        var appFolderPath = context.filesDir.absolutePath
        appFolderPath += "/images"

        val appFolder = File(appFolderPath)
        if (!appFolder.exists()) {
            appFolder.mkdirs()
        }

        val request = Request.Builder()
            .url(image.url)
            .build()

        okHttpClient.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val responseBody = response.body()
                val fileName = image.url.substring(image.url.lastIndexOf('/'))
                val file = File(appFolderPath, fileName)

                if (!file.exists()) {
                    file.createNewFile()
                }
                val outputStream = FileOutputStream(file)

                responseBody?.byteStream()?.use { inputStream ->
                    inputStream.copyTo(outputStream)
                }

                outputStream.close()
                return file.path
            } else {
                return ""
            }
        }
    }

    fun getBookmarks(): Flow<List<ImageEntity>> {
        return flow {
            emit(imageDao.getAll())
        }.flowOn(Dispatchers.IO)
    }
}