package com.andrii_a.walleria.ui.user_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.andrii_a.walleria.core.BackendResult
import com.andrii_a.walleria.domain.CollectionListLayoutType
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.PhotosListLayoutType
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.domain.repository.CollectionRepository
import com.andrii_a.walleria.domain.repository.LocalPreferencesRepository
import com.andrii_a.walleria.domain.repository.UserAccountPreferencesRepository
import com.andrii_a.walleria.domain.repository.PhotoRepository
import com.andrii_a.walleria.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

sealed interface UserLoadResult {
    data object Empty : UserLoadResult
    data object Loading : UserLoadResult
    data class Error(val userNickname: String) : UserLoadResult
    data class Success(
        val user: User,
        val loggedInUserNickname: String,
        val userPhotos: Flow<PagingData<Photo>>,
        val userLikedPhotos: Flow<PagingData<Photo>>,
        val userCollections: Flow<PagingData<Collection>>
    ) : UserLoadResult
}

@HiltViewModel
class UserDetailsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val photoRepository: PhotoRepository,
    private val collectionRepository: CollectionRepository,
    private val userAccountPreferencesRepository: UserAccountPreferencesRepository,
    localPreferencesRepository: LocalPreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _loadResult: MutableStateFlow<UserLoadResult> = MutableStateFlow(UserLoadResult.Empty)
    val loadResult: StateFlow<UserLoadResult> = _loadResult.asStateFlow()

    val photosLayoutType: StateFlow<PhotosListLayoutType> = localPreferencesRepository.photosListLayoutType
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = runBlocking { localPreferencesRepository.photosListLayoutType.first() }
        )

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

    init {
        savedStateHandle.get<String>(UserDetailsArgs.NICKNAME)?.let { nickname ->
            getUser(nickname)
        }
    }

    fun getUser(nickname: String) {
        userRepository.getUserPublicProfile(nickname).onEach { result ->
            when (result) {
                is BackendResult.Empty -> Unit
                is BackendResult.Loading -> {
                    _loadResult.update { UserLoadResult.Loading }
                }

                is BackendResult.Error -> {
                    _loadResult.update { UserLoadResult.Error(userNickname = nickname) }
                }

                is BackendResult.Success -> {
                    val user = result.value
                    _loadResult.update {
                        UserLoadResult.Success(
                            user = user,
                            loggedInUserNickname = userAccountPreferencesRepository.myProfileData.first().nickname,
                            userPhotos = photoRepository.getUserPhotos(user.username).cachedIn(viewModelScope),
                            userLikedPhotos = photoRepository.getUserLikedPhotos(user.username).cachedIn(viewModelScope),
                            userCollections = collectionRepository.getUserCollections(user.username).cachedIn(viewModelScope)
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

}