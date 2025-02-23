package com.andrii_a.walleria.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrii_a.walleria.domain.network.Resource
import com.andrii_a.walleria.domain.repository.LocalAccountRepository
import com.andrii_a.walleria.domain.repository.LoginRepository
import com.andrii_a.walleria.domain.repository.UserRepository
import com.andrii_a.walleria.ui.common.UiErrorWithRetry
import com.andrii_a.walleria.ui.common.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginRepository: LoginRepository,
    private val userRepository: UserRepository,
    private val localAccountRepository: LocalAccountRepository
) : ViewModel() {

    private val _state: MutableStateFlow<LoginUiState> = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    private val navigationEventChannel = Channel<LoginNavigationEvent>()
    val navigationEventFlow = navigationEventChannel.receiveAsFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.PerformLogin -> {
                viewModelScope.launch {
                    navigationEventChannel.send(LoginNavigationEvent.NavigateToLoginCustomTab)
                }
            }

            is LoginEvent.PerformJoin -> {
                viewModelScope.launch {
                    navigationEventChannel.send(LoginNavigationEvent.NavigateToJoinCustomTab)
                }
            }

            is LoginEvent.DismissError -> {
                _state.update {
                    it.copy(error = null)
                }
            }

            is LoginEvent.GetAccessToken -> {
                getAccessToken(code = event.code)
            }

            is LoginEvent.PerformSaveUserProfile -> {
                getAndSaveUserProfile()
            }

            is LoginEvent.GoBack -> {
                viewModelScope.launch {
                    navigationEventChannel.send(LoginNavigationEvent.NavigateBack)
                }
            }
        }
    }

    private fun getAccessToken(code: String) {
        loginRepository.login(code)
            .onEach { result ->
                when (result) {
                    Resource.Empty -> Unit
                    Resource.Loading -> {
                        _state.update {
                            it.copy(
                                isLoading = true,
                                isTokenObtained = false,
                                isUserDataSaved = false,
                                error = null
                            )
                        }
                    }

                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isTokenObtained = false,
                                isUserDataSaved = false,
                                error = UiErrorWithRetry(
                                    reason = UiText.DynamicString(result.reason.orEmpty())
                                )
                            )
                        }
                    }

                    is Resource.Success -> {
                        val token = result.value
                        localAccountRepository.saveAccessToken(token)
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isTokenObtained = true,
                                isUserDataSaved = false,
                                error = null
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun getAndSaveUserProfile() {
        userRepository.getPrivateUserProfile()
            .onEach { result ->
                when (result) {
                    is Resource.Empty -> Unit
                    is Resource.Loading -> {
                        _state.update {
                            it.copy(
                                isLoading = true,
                                isUserDataSaved = false,
                                error = null
                            )
                        }
                    }

                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isUserDataSaved = false,
                                error = UiErrorWithRetry(
                                    reason = UiText.DynamicString(result.reason.orEmpty())
                                )
                            )
                        }
                    }

                    is Resource.Success -> {
                        localAccountRepository.saveAccountInfo(userPrivateProfile = result.value)
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isUserDataSaved = true,
                                error = null
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }
}
