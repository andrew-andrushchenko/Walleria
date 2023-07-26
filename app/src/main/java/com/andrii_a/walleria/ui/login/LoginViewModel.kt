package com.andrii_a.walleria.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrii_a.walleria.core.ApplicationScope
import com.andrii_a.walleria.core.BackendResult
import com.andrii_a.walleria.domain.models.login.AccessToken
import com.andrii_a.walleria.domain.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LoginState {
    object Empty : LoginState
    object Loading : LoginState
    object Error : LoginState
    data class Success(val accessToken: AccessToken) : LoginState
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel() {

    val loginUrl = loginRepository.loginUrl

    val joinUrl = loginRepository.joinUrl

    private val _loginState: MutableStateFlow<LoginState> = MutableStateFlow(LoginState.Empty)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun getAccessToken(code: String) {
        flow {
            emit(BackendResult.Loading)
            val accessTokenResult = loginRepository.getAccessToken(code)
            emit(accessTokenResult)
        }.onEach { backendResult ->
            _loginState.update {
                when (backendResult) {
                    is BackendResult.Empty -> LoginState.Empty
                    is BackendResult.Loading -> LoginState.Loading
                    is BackendResult.Error -> LoginState.Error
                    is BackendResult.Success -> LoginState.Success(backendResult.value)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = BackendResult.Empty
        ).launchIn(viewModelScope)
    }

    fun retrieveAndSaveUserData(accessToken: AccessToken) {
        applicationScope.launch {
            saveAccessToken(accessToken = accessToken)
            getAndSaveUserProfile()
        }
    }

    private suspend fun saveAccessToken(accessToken: AccessToken) {
        loginRepository.saveAccessToken(accessToken = accessToken)
    }

    private suspend fun getAndSaveUserProfile() {
        loginRepository.getMyProfile().let { backendResult ->
            if (backendResult is BackendResult.Success) {
                loginRepository.saveMyProfile(myProfile = backendResult.value)
            }
        }
    }
}