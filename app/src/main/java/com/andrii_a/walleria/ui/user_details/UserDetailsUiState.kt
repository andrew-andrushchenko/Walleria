package com.andrii_a.walleria.ui.user_details

import androidx.compose.runtime.Stable
import androidx.paging.PagingData
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.ui.common.UiError
import com.andrii_a.walleria.ui.common.UserNickname
import com.andrii_a.walleria.ui.util.emptyPagingData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Stable
data class UserDetailsUiState(
    val loggedInUserNickname: UserNickname = "",
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: UiError? = null,
    val isDetailsDialogOpened: Boolean = false,
    private val photosPagingData: PagingData<Photo> = emptyPagingData(),
    private val likedPhotosPagingData: PagingData<Photo> = emptyPagingData(),
    private val collectionsPagingData: PagingData<Collection> = emptyPagingData(),
    val photosLoadQuality: PhotoQuality = PhotoQuality.MEDIUM,
) {
    private val _photos: MutableStateFlow<PagingData<Photo>> = MutableStateFlow(emptyPagingData())
    val photos: StateFlow<PagingData<Photo>> = _photos.asStateFlow()

    private val _likedPhotos: MutableStateFlow<PagingData<Photo>> = MutableStateFlow(emptyPagingData())
    val likedPhotos: StateFlow<PagingData<Photo>> = _likedPhotos.asStateFlow()

    private val _collections: MutableStateFlow<PagingData<Collection>> = MutableStateFlow(
        emptyPagingData()
    )
    val collections: StateFlow<PagingData<Collection>> = _collections.asStateFlow()

    init {
        _photos.update { photosPagingData }
        _likedPhotos.update { likedPhotosPagingData }
        _collections.update { collectionsPagingData }
    }
}
