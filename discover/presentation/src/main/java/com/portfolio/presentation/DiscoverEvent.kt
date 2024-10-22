package com.portfolio.presentation

import com.portfolio.core.presentation.ui.UiText

sealed interface DiscoverEvent {
    data class Error(val error: UiText): DiscoverEvent
    data object AuthError: DiscoverEvent
}