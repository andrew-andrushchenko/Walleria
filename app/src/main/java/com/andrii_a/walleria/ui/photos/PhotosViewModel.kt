package com.andrii_a.walleria.ui.photos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.andrii_a.walleria.domain.PhotoListDisplayOrder
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.PhotosListLayoutType
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.repository.LocalPreferencesRepository
import com.andrii_a.walleria.domain.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class PhotosViewModel @Inject constructor(
    private val photoRepository: PhotoRepository,
    localPreferencesRepository: LocalPreferencesRepository
) : ViewModel() {

    val photosLayoutType: StateFlow<PhotosListLayoutType> = localPreferencesRepository.photosListLayoutType
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = runBlocking { localPreferencesRepository.photosListLayoutType.first() }
        )

    val photosLoadQuality: StateFlow<PhotoQuality> = localPreferencesRepository.photoPreviewsQuality
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = runBlocking { localPreferencesRepository.photoPreviewsQuality.first() }
        )

    private val _order: MutableStateFlow<PhotoListDisplayOrder> = MutableStateFlow(PhotoListDisplayOrder.LATEST)
    val order: StateFlow<PhotoListDisplayOrder> = _order.asStateFlow()

    val photos: Flow<PagingData<Photo>> = _order.flatMapLatest { order ->
        photoRepository.getPhotos(order).cachedIn(viewModelScope)
    }

    fun orderBy(orderOptionOrdinalNum: Int) {
        _order.update { PhotoListDisplayOrder.values()[orderOptionOrdinalNum] }
    }
}