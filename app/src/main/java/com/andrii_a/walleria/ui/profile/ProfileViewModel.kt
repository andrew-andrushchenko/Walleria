package com.andrii_a.walleria.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrii_a.walleria.domain.models.preferences.UserPrivateProfileData
import com.andrii_a.walleria.domain.repository.UserAccountPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userAccountPreferencesRepository: UserAccountPreferencesRepository
) : ViewModel() {

    val isUserLoggedIn: StateFlow<Boolean> = userAccountPreferencesRepository.isUserLoggedIn
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = false
        )

    val userPrivateProfileData: StateFlow<UserPrivateProfileData> = userAccountPreferencesRepository.userPrivateProfileData
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = UserPrivateProfileData()
        )

    fun logout() {
        viewModelScope.launch {
            userAccountPreferencesRepository.clearAccountInfo()
        }
    }
}