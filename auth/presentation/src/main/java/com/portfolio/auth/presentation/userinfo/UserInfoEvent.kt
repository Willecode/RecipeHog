package com.portfolio.auth.presentation.userinfo

import com.portfolio.core.presentation.ui.UiText

sealed interface UserInfoEvent {
    data class UserInfoError(val error: UiText): UserInfoEvent
    data object UserInfoUpdateSuccess: UserInfoEvent
}