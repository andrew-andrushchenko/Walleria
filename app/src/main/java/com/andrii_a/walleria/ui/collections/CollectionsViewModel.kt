package com.andrii_a.walleria.ui.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.andrii_a.walleria.domain.repository.CollectionRepository
import com.andrii_a.walleria.domain.models.collection.Collection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class CollectionsViewModel @Inject constructor(
    collectionRepository: CollectionRepository
) : ViewModel() {

    val collections: Flow<PagingData<Collection>> =
        collectionRepository.getCollections().cachedIn(viewModelScope)

}