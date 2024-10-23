package com.portfolio.recipehog

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portfolio.auth.domain.AuthRepository
import com.portfolio.core.domain.model.SessionStorage
import com.portfolio.core.domain.util.Result
import kotlinx.coroutines.launch

class MainViewModel(
    private val sessionStorage: SessionStorage,
    private val authRepository: AuthRepository
): ViewModel() {

    var state by mutableStateOf(MainState())
        private set

    init {
        viewModelScope.launch {
            state = state.copy(isCheckingAuth = true)
            val userInfo = sessionStorage.get()
            state = state.copy(
                isLoggedIn = userInfo != null,
                isCheckingAuth = false
            )
        }
    }

    fun hasUsername(): Boolean {
        sessionStorage.get()?.userName ?: return false
        return true
    }

    fun logOut(onSuccessfulLogout: () -> Unit) {
        viewModelScope.launch {
            val result = authRepository.signOut()
            when(result) {
                is Result.Error -> {
                    val userInfo = sessionStorage.get()
                    if (userInfo == null) {
                        state = state.copy(isLoggedIn = false)
                        onSuccessfulLogout()
                    }
                    else
                        Unit //TODO: Is there a case where this can happen? If so, display some error msg in UI
                }
                is Result.Success -> {
                    state = state.copy(isLoggedIn = false)
                    onSuccessfulLogout()
                }
            }
        }
    }
}