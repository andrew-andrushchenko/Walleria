package com.andrii_a.walleria.ui.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.andrii_a.walleria.domain.CollectionListLayoutType
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.repository.CollectionRepository
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.repository.LocalPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class CollectionsViewModel @Inject constructor(
    collectionRepository: CollectionRepository,
    localPreferencesRepository: LocalPreferencesRepository
) : ViewModel() {

    val collectionsLayoutType: StateFlow<CollectionListLayoutType> = localPreferencesRepository.collectionsListLayoutType
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = runBlocking { localPreferencesRepository.collectionsListLayoutType.first() }
        )

    val photosLoadQuality: StateFlow<PhotoQuality> = localPreferencesRepository.photosLoadQuality
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = runBlocking { localPreferencesRepository.photosLoadQuality.first() }
        )

    val collections: Flow<PagingData<Collection>> =
        collectionRepository.getCollections().cachedIn(viewModelScope)

}