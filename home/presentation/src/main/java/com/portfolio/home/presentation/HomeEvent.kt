package com.portfolio.home.presentation

import com.portfolio.core.presentation.ui.UiText

interface HomeEvent {
    data class HomeError(val error: UiText): HomeEvent
    data object AuthError: HomeEvent

}