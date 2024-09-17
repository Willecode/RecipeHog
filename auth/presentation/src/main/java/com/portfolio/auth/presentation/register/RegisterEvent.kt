package com.portfolio.auth.presentation.register

import com.portfolio.core.presentation.ui.UiText

sealed interface RegisterEvent{
    data class RegisterError(val error: UiText): RegisterEvent
    data object RegisterSuccess: RegisterEvent
}
