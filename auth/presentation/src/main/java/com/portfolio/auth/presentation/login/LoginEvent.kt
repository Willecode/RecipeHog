package com.portfolio.auth.presentation.login

import com.portfolio.core.presentation.ui.UiText

interface LoginEvent {
    data class LoginError(val error: UiText): LoginEvent
    data object LoginSuccess: LoginEvent
}