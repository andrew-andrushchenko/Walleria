package com.andrii_a.walleria.ui.user_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.andrii_a.walleria.core.BackendResult
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.domain.repository.CollectionRepository
import com.andrii_a.walleria.domain.repository.LocalUserAccountPreferencesRepository
import com.andrii_a.walleria.domain.repository.PhotoRepository
import com.andrii_a.walleria.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
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
    private val localUserAccountPreferencesRepository: LocalUserAccountPreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _loadResult: MutableStateFlow<UserLoadResult> = MutableStateFlow(UserLoadResult.Empty)
    val loadResult: StateFlow<UserLoadResult> = _loadResult.asStateFlow()

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
                            loggedInUserNickname = localUserAccountPreferencesRepository.myProfileData.first().nickname,
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