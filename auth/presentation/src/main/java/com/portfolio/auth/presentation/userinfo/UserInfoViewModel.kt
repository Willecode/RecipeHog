package com.portfolio.auth.presentation.userinfo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portfolio.auth.domain.AuthRepository
import com.portfolio.core.domain.util.Result
import com.portfolio.core.presentation.ui.asUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class UserInfoViewModel(
    private val authRepository: AuthRepository
): ViewModel() {

    var state by mutableStateOf(UserInfoState())
        private set

    private val _eventChannel = Channel<UserInfoEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    fun onAction(action: UserInfoAction) {
        when (action) {
            UserInfoAction.OnUserNameUpdateClick -> updateUserName()
        }
    }

    private fun updateUserName() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            when (val result = authRepository.setUserName(state.username.text.toString())) {
                is Result.Error -> {
                    _eventChannel.send(UserInfoEvent.UserInfoError(result.error.asUiText()))
                }
                is Result.Success -> {
                    _eventChannel.send(UserInfoEvent.UserInfoUpdateSuccess)
                }
            }
            state = state.copy(isLoading = false)
        }
    }
}