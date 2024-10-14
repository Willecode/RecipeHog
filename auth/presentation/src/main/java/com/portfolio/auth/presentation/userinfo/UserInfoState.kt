package com.portfolio.auth.presentation.userinfo

import androidx.compose.foundation.text.input.TextFieldState

data class UserInfoState(
    val username: TextFieldState = TextFieldState(),
    val isLoading: Boolean = false
    )
