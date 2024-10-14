package com.portfolio.recipehog

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portfolio.core.domain.model.SessionStorage
import kotlinx.coroutines.launch

class MainViewModel(
    private val sessionStorage: SessionStorage
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

}