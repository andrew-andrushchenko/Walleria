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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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

    val photosLoadQuality: StateFlow<PhotoQuality> = localPreferencesRepository.photosLoadQuality
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = runBlocking { localPreferencesRepository.photosLoadQuality.first() }
        )

    private val _order: MutableStateFlow<PhotoListDisplayOrder> = MutableStateFlow(PhotoListDisplayOrder.LATEST)
    val order: StateFlow<PhotoListDisplayOrder> = _order.asStateFlow()

    val photos: Flow<PagingData<Photo>> = _order.flatMapLatest { order ->
        photoRepository.getPhotos(order)
    }.cachedIn(viewModelScope)

    fun orderBy(orderOptionOrdinalNum: Int) {
        _order.update { PhotoListDisplayOrder.entries[orderOptionOrdinalNum] }
    }
}