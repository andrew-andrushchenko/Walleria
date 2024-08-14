package com.andrii_a.walleria.ui.user_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.andrii_a.walleria.domain.network.BackendResult
import com.andrii_a.walleria.domain.repository.CollectionRepository
import com.andrii_a.walleria.domain.repository.LocalPreferencesRepository
import com.andrii_a.walleria.domain.repository.PhotoRepository
import com.andrii_a.walleria.domain.repository.UserAccountPreferencesRepository
import com.andrii_a.walleria.domain.repository.UserRepository
import com.andrii_a.walleria.ui.common.UiErrorWithRetry
import com.andrii_a.walleria.ui.common.UiText
import com.andrii_a.walleria.ui.common.UserNickname
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val photoRepository: PhotoRepository,
    private val collectionRepository: CollectionRepository,
    userAccountPreferencesRepository: UserAccountPreferencesRepository,
    localPreferencesRepository: LocalPreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state: MutableStateFlow<UserDetailsUiState> =
        MutableStateFlow(UserDetailsUiState())
    val state = combine(
        userAccountPreferencesRepository.userPrivateProfileData,
        localPreferencesRepository.photosListLayoutType,
        localPreferencesRepository.collectionsListLayoutType,
        localPreferencesRepository.photosLoadQuality,
        _state
    ) { userPrivateProfileData, photosListLayoutType, collectionsListLayoutType, photosLoadQuality, state ->
        state.copy(
            loggedInUserNickname = userPrivateProfileData.nickname,
            photosListLayoutType = photosListLayoutType,
            collectionListLayoutType = collectionsListLayoutType,
            photosLoadQuality = photosLoadQuality
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = _state.value
    )

    private val navigationChannel = Channel<UserDetailsNavigationEvent>()
    val navigationEventsChannelFlow = navigationChannel.receiveAsFlow()

    init {
        savedStateHandle.get<String>(UserDetailsArgs.NICKNAME)?.let { userNickname ->
            getUser(userNickname)
        }
    }

    fun onEvent(event: UserDetailsEvent) {
        when (event) {
            is UserDetailsEvent.RequestUser -> {
                getUser(event.userNickname)
            }

            is UserDetailsEvent.SelectPhoto -> {
                viewModelScope.launch {
                    navigationChannel.send(
                        UserDetailsNavigationEvent.NavigateToPhotoDetailsScreen(
                            event.photoId
                        )
                    )
                }
            }

            is UserDetailsEvent.SelectCollection -> {
                viewModelScope.launch {
                    navigationChannel.send(
                        UserDetailsNavigationEvent.NavigateToCollectionDetails(
                            event.collectionId
                        )
                    )
                }
            }

            is UserDetailsEvent.SelectUser -> {
                viewModelScope.launch {
                    navigationChannel.send(UserDetailsNavigationEvent.NavigateToUserDetails(event.userNickname))
                }
            }

            is UserDetailsEvent.SearchByTag -> {
                viewModelScope.launch {
                    navigationChannel.send(UserDetailsNavigationEvent.NavigateToSearchScreen(event.query))
                }
            }

            is UserDetailsEvent.SelectEditProfile -> {
                viewModelScope.launch {
                    navigationChannel.send(UserDetailsNavigationEvent.NavigateToEditProfile)
                }
            }

            is UserDetailsEvent.OpenUserProfileInBrowser -> {
                viewModelScope.launch {
                    navigationChannel.send(UserDetailsNavigationEvent.NavigateToUserProfileInChromeTab(event.userNickname))
                }
            }

            is UserDetailsEvent.GoBack -> {
                viewModelScope.launch {
                    navigationChannel.send(UserDetailsNavigationEvent.NavigateBack)
                }
            }

            is UserDetailsEvent.OpenDetailsDialog -> {
                _state.update {
                    it.copy(isDetailsDialogOpened = true)
                }
            }

            is UserDetailsEvent.DismissDetailsDialog -> {
                _state.update {
                    it.copy(isDetailsDialogOpened = false)
                }
            }

            is UserDetailsEvent.SelectInstagramProfile -> {
                viewModelScope.launch {
                    navigationChannel.send(UserDetailsNavigationEvent.NavigateToInstagramApp(event.instagramNickname))
                }
            }

            is UserDetailsEvent.SelectTwitterProfile -> {
                viewModelScope.launch {
                    navigationChannel.send(UserDetailsNavigationEvent.NavigateToTwitterApp(event.twitterNickname))
                }
            }

            is UserDetailsEvent.SelectPortfolioLink -> {
                viewModelScope.launch {
                    navigationChannel.send(UserDetailsNavigationEvent.NavigateToChromeCustomTab(event.url))
                }
            }
        }
    }

    private fun getUser(userNickname: UserNickname) {
        userRepository.getUserPublicProfile(userNickname).onEach { result ->
            when (result) {
                is BackendResult.Empty -> Unit
                is BackendResult.Loading -> {
                    _state.update {
                        it.copy(isLoading = true)
                    }
                }

                is BackendResult.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = UiErrorWithRetry(
                                reason = UiText.DynamicString(result.reason.orEmpty()),
                                onRetry = { onEvent(UserDetailsEvent.RequestUser(userNickname)) }
                            )
                        )
                    }
                }

                is BackendResult.Success -> {
                    val user = result.value
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            user = user,
                            photos = photoRepository.getUserPhotos(user.username)
                                .cachedIn(viewModelScope),
                            likedPhotos = photoRepository.getUserLikedPhotos(user.username)
                                .cachedIn(viewModelScope),
                            collections = collectionRepository.getUserCollections(user.username)
                                .cachedIn(viewModelScope)
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

}