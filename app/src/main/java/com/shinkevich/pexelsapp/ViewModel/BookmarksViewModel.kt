package com.shinkevich.pexelsapp.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shinkevich.pexelsapp.Model.Database.ImageEntity
import com.shinkevich.pexelsapp.Model.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    val bookmarks = MutableLiveData<List<ImageEntity>>()
    val dataIsLoading = MutableLiveData<Boolean>()
    val imageForDetails = MutableLiveData<Long>()
    var imageForDetailsHandled = false

    fun getBookmarks(){
        updateDataIsLoading(true)
        viewModelScope.launch {
            repository.getBookmarks().collect{ bookmarksList ->
                bookmarks.postValue(bookmarksList)
            }
        }
    }

    fun updateDataIsLoading(isLoading: Boolean) {
        dataIsLoading.postValue(isLoading)
    }

    fun setImageForDetails(imageId: Long){
        imageForDetailsHandled = false
        imageForDetails.postValue(imageId)
    }
}