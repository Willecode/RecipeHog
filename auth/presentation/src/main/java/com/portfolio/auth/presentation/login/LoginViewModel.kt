package com.portfolio.auth.presentation.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portfolio.auth.domain.AuthRepository
import com.portfolio.auth.presentation.R
import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.Result
import com.portfolio.core.presentation.ui.UiText
import com.portfolio.core.presentation.ui.asUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    var state by mutableStateOf(LoginState())
    private set

    private val _eventChannel = Channel<LoginEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.OnLoginClicked -> login()
            is LoginAction.OnPasswordVisibilityChanged -> setPasswordVisibility(action.visible)
            else -> Unit
        }
    }

    private fun setPasswordVisibility(visible: Boolean) {
        state = state.copy(isPasswordVisible = visible)
    }

    private fun login() {
        viewModelScope.launch {
            state = state.copy(isLoggingIn = true)
            val result = authRepository.login(
                state.email.text.toString(),
                state.password.text.toString()
            )
            when (result) {
                is Result.Error -> {
                    if (result.error == DataError.Network.UNAUTHORIZED) {
                        _eventChannel.send(LoginEvent.LoginError(UiText.StringResource(R.string.incorrect_credentials)))
                    } else {
                        _eventChannel.send(LoginEvent.LoginError(result.error.asUiText()))
                    }
                }
                is Result.Success -> {
                    _eventChannel.send(LoginEvent.LoginSuccess)
                    state = state.copy(isLoggingIn = false)
                }
            }
            state = state.copy(isLoggingIn = false)
        }
    }
}