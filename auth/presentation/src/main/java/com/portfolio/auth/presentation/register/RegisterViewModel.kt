package com.portfolio.auth.presentation.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portfolio.auth.domain.AuthRepository
import com.portfolio.auth.domain.RegisterCredentialValidator
import com.portfolio.auth.presentation.register.RegisterAction.OnEmailChanged
import com.portfolio.auth.presentation.register.RegisterAction.OnPasswordChanged
import com.portfolio.auth.presentation.register.RegisterAction.OnPasswordVisibilityChanged
import com.portfolio.auth.presentation.register.RegisterAction.OnRegisterClicked
import com.portfolio.core.domain.util.Result
import com.portfolio.core.presentation.ui.asUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerCredentialValidator: RegisterCredentialValidator,
    private val authRepository: AuthRepository
) : ViewModel() {

    var state by mutableStateOf(RegisterState())
        private set

    private val _eventChannel = Channel<RegisterEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    /**
     * Observes changes to password text and validates it. See this:
     * https://developer.android.com/reference/kotlin/androidx/compose/foundation/text/input/TextFieldState#text()
     */
    suspend fun observeAndValidatePassword() {
        snapshotFlow { state.password.text }
            .collectLatest { password ->
                state = state.copy(
                    passwordValidationState =
                        registerCredentialValidator.validatePassword(password.toString())
                )
            }
    }

    suspend fun observeAndValidateEmail() {
        snapshotFlow { state.email.text }
            .collectLatest { email ->
                state = state.copy(
                    isEmailValid = registerCredentialValidator.isValidEmail(email.toString())
                )
            }
    }


    fun onAction(action: RegisterAction) {
        when (action) {
            is OnRegisterClicked -> register()
            is OnEmailChanged -> validateEmail()
            is OnPasswordChanged -> validatePassword()
            is OnPasswordVisibilityChanged -> togglePasswordVisibility()
        }
    }

    private fun togglePasswordVisibility() {
        state = state.copy(isPasswordVisible = !state.isPasswordVisible)
    }

    private fun validatePassword() {
        state = state.copy(
            passwordValidationState = registerCredentialValidator.validatePassword(
                password = state.password.text.toString()
            )
        )
    }

    private fun validateEmail() {
        state = state.copy(
            isEmailValid = registerCredentialValidator.isValidEmail(
                email = state.email.text.toString()
            )
        )
    }

    private fun register() {
        viewModelScope.launch {
            state = state.copy(isRegistering = true)
            val result = authRepository.register(
                email = state.email.text.toString(),
                password = state.password.text.toString()
            )

            when (result) {
                is Result.Error -> {
                    _eventChannel.send(RegisterEvent.RegisterError(result.error.asUiText()))
                }
                is Result.Success -> {
                    _eventChannel.send(RegisterEvent.RegisterSuccess)
                }
            }
            state = state.copy(isRegistering = false)
        }
    }

}