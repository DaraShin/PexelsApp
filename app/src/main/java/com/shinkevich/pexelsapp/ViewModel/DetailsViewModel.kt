package com.shinkevich.pexelsapp.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shinkevich.pexelsapp.Model.Database.ImageEntity
import com.shinkevich.pexelsapp.Model.Image
import com.shinkevich.pexelsapp.Model.Repository
import com.shinkevich.pexelsapp.Model.RequestResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    val photoResult = MutableLiveData<RequestResult<Image>>()
    val bookmarkResult = MutableLiveData<RequestResult<ImageEntity>>()

    fun getPhoto(photoId: Long) {
        viewModelScope.launch {
            repository.getPhotoById(photoId).collect { photoRes ->
                photoResult.postValue(photoRes)
            }
        }
    }

    fun getBookmark(photoId: Long) {
        viewModelScope.launch {
            repository.getBookmarkById(photoId).collect { photoRes ->
                bookmarkResult.postValue(RequestResult.Success(photoRes))
            }
        }
    }

    fun downloadPhoto(image: Image) {
        repository.downloadPhoto(image)
    }

    fun savePhotoToBookmarks(image: Image) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveImageToBookmarks(image)
        }
    }
}