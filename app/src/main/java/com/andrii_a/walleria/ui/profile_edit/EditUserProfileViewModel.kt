package com.andrii_a.walleria.ui.profile_edit

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.models.preferences.UserPrivateProfileData
import com.andrii_a.walleria.domain.network.Resource
import com.andrii_a.walleria.domain.repository.LoginRepository
import com.andrii_a.walleria.domain.repository.UserAccountPreferencesRepository
import com.andrii_a.walleria.ui.common.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class EditUserProfileViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    userAccountPreferencesRepository: UserAccountPreferencesRepository
) : ViewModel() {

    private val _state: MutableStateFlow<EditUserProfileUiState> =
        MutableStateFlow(EditUserProfileUiState())
    val state = combine(
        userAccountPreferencesRepository.userPrivateProfileData,
        _state
    ) { userPrivateProfileData, state ->
        state.copy(
            nickname = userPrivateProfileData.nickname,
            firstName = userPrivateProfileData.firstName,
            lastName = userPrivateProfileData.lastName,
            email = userPrivateProfileData.email,
            portfolioLink = userPrivateProfileData.portfolioLink,
            instagramUsername = userPrivateProfileData.instagramUsername,
            location = userPrivateProfileData.location,
            bio = userPrivateProfileData.bio
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = _state.value
    )

    private val _profileUpdateMessageFlow: MutableSharedFlow<UiText> = MutableSharedFlow()
    val profileUpdateMessageFlow: SharedFlow<UiText> = _profileUpdateMessageFlow.asSharedFlow()

    private val navigationChannel = Channel<EditUserProfileNavigationEvent>()
    val navigationEventsChannelFlow = navigationChannel.receiveAsFlow()

    fun onEvent(event: EditUserProfileEvent) {
        when (event) {
            is EditUserProfileEvent.NicknameChanged -> {
                val isNicknameValid = validateNickname(event.value)
                _state.update {
                    it.copy(nickname = event.value, isNicknameValid = isNicknameValid)
                }
            }

            is EditUserProfileEvent.FirstNameChanged -> {
                _state.update {
                    it.copy(firstName = event.value)
                }
            }

            is EditUserProfileEvent.LastNameChanged -> {
                _state.update {
                    it.copy(lastName = event.value)
                }
            }

            is EditUserProfileEvent.EmailChanged -> {
                val isEmailValid = validateEmail(event.value)
                _state.update {
                    it.copy(email = event.value, isEmailValid = isEmailValid)
                }
            }

            is EditUserProfileEvent.PortfolioLinkChanged -> {
                _state.update {
                    it.copy(portfolioLink = event.value)
                }
            }

            is EditUserProfileEvent.InstagramUsernameChanged -> {
                _state.update {
                    it.copy(instagramUsername = event.value)
                }
            }

            is EditUserProfileEvent.LocationChanged -> {
                _state.update {
                    it.copy(location = event.value)
                }
            }

            is EditUserProfileEvent.BioChanged -> {
                _state.update {
                    it.copy(bio = event.value)
                }
            }

            is EditUserProfileEvent.SaveProfile -> {
                with(_state.value) {
                    val userPrivateProfileData = UserPrivateProfileData(
                        nickname = nickname,
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        portfolioLink = portfolioLink,
                        instagramUsername = instagramUsername,
                        location = location,
                        bio = bio
                    )

                    saveUserProfileData(userPrivateProfileData)
                }
            }

            is EditUserProfileEvent.GoBack -> {
                viewModelScope.launch {
                    navigationChannel.send(EditUserProfileNavigationEvent.NavigateBack)
                }
            }
        }
    }

    private fun validateNickname(nickname: String): Boolean =
        Pattern.compile("([A-Za-z0-9_]+)").matcher(nickname).matches()

    private fun validateEmail(emailAddress: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()

    private fun saveUserProfileData(userPrivateProfileData: UserPrivateProfileData) {
        viewModelScope.launch {
            val updateResult = loginRepository.updatePrivateUserProfile(userPrivateProfileData)

            when (updateResult) {
                is Resource.Empty, is Resource.Loading -> Unit
                is Resource.Error -> {
                    _profileUpdateMessageFlow.emit(UiText.StringResource(R.string.profile_data_not_updated))
                }

                is Resource.Success -> {
                    loginRepository.savePrivateUserProfile(updateResult.value)
                    _profileUpdateMessageFlow.emit(UiText.StringResource(R.string.profile_data_updated_successfully))
                }
            }
        }
    }
}