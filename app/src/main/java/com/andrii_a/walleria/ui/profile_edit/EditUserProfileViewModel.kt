package com.andrii_a.walleria.ui.profile_edit

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrii_a.walleria.R
import com.andrii_a.walleria.core.BackendResult
import com.andrii_a.walleria.domain.models.preferences.MyProfileData
import com.andrii_a.walleria.domain.repository.LocalUserAccountPreferencesRepository
import com.andrii_a.walleria.domain.repository.LoginRepository
import com.andrii_a.walleria.ui.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.regex.Pattern
import javax.inject.Inject

data class EditUserProfileScreenState(
    val nickname: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val portfolioLink: String = "",
    val instagramUsername: String = "",
    val location: String = "",
    val bio: String = "",
    val isNicknameValid: Boolean = true,
    val isEmailValid: Boolean = true
) {
    val isInputValid: Boolean
        get() = isNicknameValid && isEmailValid
}

sealed interface EditUserProfileEvent {
    data class NicknameChanged(val value: String) : EditUserProfileEvent
    data class FirstNameChanged(val value: String) : EditUserProfileEvent
    data class LastNameChanged(val value: String) : EditUserProfileEvent
    data class EmailChanged(val value: String) : EditUserProfileEvent
    data class PortfolioLinkChanged(val value: String) : EditUserProfileEvent
    data class InstagramUsernameChanged(val value: String) : EditUserProfileEvent
    data class LocationChanged(val value: String) : EditUserProfileEvent
    data class BioChanged(val value: String) : EditUserProfileEvent
    data object SaveProfile : EditUserProfileEvent
}

@HiltViewModel
class EditUserProfileViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    localUserAccountPreferencesRepository: LocalUserAccountPreferencesRepository
) : ViewModel() {

    private val _state: MutableStateFlow<EditUserProfileScreenState> =
        MutableStateFlow(
            runBlocking {
                localUserAccountPreferencesRepository.myProfileData.map {
                    EditUserProfileScreenState(
                        nickname = it.nickname,
                        firstName = it.firstName,
                        lastName = it.lastName,
                        email = it.email,
                        portfolioLink = it.portfolioLink,
                        instagramUsername = it.instagramUsername,
                        location = it.location,
                        bio = it.bio
                    )
                }.first()
            }
        )
    val state: StateFlow<EditUserProfileScreenState> = _state.asStateFlow()

    private val _profileUpdateMessageFlow: MutableSharedFlow<UiText> = MutableSharedFlow()
    val profileUpdateMessageFlow: SharedFlow<UiText> = _profileUpdateMessageFlow.asSharedFlow()

    fun onEvent(event: EditUserProfileEvent) {
        when (event) {
            is EditUserProfileEvent.NicknameChanged -> {
                val isNicknameValid = validateNickname(event.value)
                _state.update {
                    _state.value.copy(nickname = event.value, isNicknameValid = isNicknameValid)
                }
            }

            is EditUserProfileEvent.FirstNameChanged -> {
                _state.update { _state.value.copy(firstName = event.value) }
            }

            is EditUserProfileEvent.LastNameChanged -> {
                _state.update { _state.value.copy(lastName = event.value) }
            }

            is EditUserProfileEvent.EmailChanged -> {
                val isEmailValid = validateEmail(event.value)
                _state.update {
                    _state.value.copy(email = event.value, isEmailValid = isEmailValid)
                }
            }

            is EditUserProfileEvent.PortfolioLinkChanged -> {
                _state.update { _state.value.copy(portfolioLink = event.value) }
            }

            is EditUserProfileEvent.InstagramUsernameChanged -> {
                _state.update { _state.value.copy(instagramUsername = event.value) }
            }

            is EditUserProfileEvent.LocationChanged -> {
                _state.update { _state.value.copy(location = event.value) }
            }

            is EditUserProfileEvent.BioChanged -> {
                _state.update { _state.value.copy(bio = event.value) }
            }

            is EditUserProfileEvent.SaveProfile -> {
                with(_state.value) {
                    val myProfileData = MyProfileData(
                        nickname = nickname,
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        portfolioLink = portfolioLink,
                        instagramUsername = instagramUsername,
                        location = location,
                        bio = bio
                    )

                    saveUserProfileData(myProfileData)
                }
            }
        }
    }

    private fun validateNickname(nickname: String): Boolean =
        Pattern.compile("([A-Za-z0-9_]+)").matcher(nickname).matches()

    private fun validateEmail(emailAddress: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()

    private fun saveUserProfileData(myProfileData: MyProfileData) {
        viewModelScope.launch {
            val updateResult = loginRepository.updateMyProfile(myProfileData)

            when (updateResult) {
                is BackendResult.Empty, is BackendResult.Loading -> Unit
                is BackendResult.Error -> {
                    _profileUpdateMessageFlow.emit(UiText.StringResource(R.string.profile_data_not_updated))
                }

                is BackendResult.Success -> {
                    loginRepository.saveMyProfile(updateResult.value)
                    _profileUpdateMessageFlow.emit(UiText.StringResource(R.string.profile_data_updated_successfully))
                }
            }
        }
    }
}