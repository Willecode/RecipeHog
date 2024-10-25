package com.portfolio.profile.presentation.view_profile

import com.portfolio.core.presentation.ui.UiText

interface ViewProfileEvent {
    data class ViewProfileError(val error: UiText): ViewProfileEvent
    data object AuthError: ViewProfileEvent
}