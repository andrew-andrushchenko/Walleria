package com.andrii_a.walleria.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrii_a.walleria.domain.models.preferences.MyProfileData
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

    val isUserLoggedIn: StateFlow<Boolean> = userAccountPreferencesRepository.isUserAuthorized
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = false
        )

    val myProfileData: StateFlow<MyProfileData> = userAccountPreferencesRepository.myProfileData
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = MyProfileData()
        )

    fun logout() {
        viewModelScope.launch {
            userAccountPreferencesRepository.reset()
        }
    }
}