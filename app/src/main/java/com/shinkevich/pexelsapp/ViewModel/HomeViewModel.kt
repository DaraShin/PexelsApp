package com.shinkevich.pexelsapp.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shinkevich.pexelsapp.Model.Image
import com.shinkevich.pexelsapp.Model.Repository
import com.shinkevich.pexelsapp.Model.RequestResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    val imagesToShowResult = MutableLiveData<RequestResult<List<Image>>>()
    val featuredCollections = MutableLiveData<RequestResult<List<String>>>()
    val dataIsLoading = MutableLiveData<Boolean>()
    val popularQuerySearch = MutableLiveData<String>()
    var popularQuerySearchHandled = false
    val imageForDetails = MutableLiveData<Long>()
    var imageForDetailsHandled = false

    var isFirstLoad = true

    var selectedPopularQueryPosition : Int = -1

    var querySearchHandled = false


    fun searchImages(searchQuery: String) {
        updateDataIsLoading(true)
        viewModelScope.launch {
            repository.searchImages(searchQuery).collect { searchImagesResult ->
                imagesToShowResult.postValue(searchImagesResult)
            }
        }
    }

    fun getPopularImages() {
        updateDataIsLoading(true)
        viewModelScope.launch {
            repository.getPopularImages().collect { popularImagesResult ->
                imagesToShowResult.postValue(popularImagesResult)
            }
        }
    }

    fun getFeaturedCollections() {
        updateDataIsLoading(true)
        viewModelScope.launch {
            repository.getFeaturedCollections().collect { featuredCollectionsResult ->
                featuredCollections.postValue(featuredCollectionsResult)
            }
        }
    }

    fun updateDataIsLoading(isLoading: Boolean) {
        dataIsLoading.postValue(isLoading)
    }

    fun setPopularQuerySearch(query: String) {
        popularQuerySearchHandled = false
        popularQuerySearch.postValue(query)
    }

    fun setImageForDetails(imageId: Long){
        imageForDetailsHandled = false
        imageForDetails.postValue(imageId)
    }
}