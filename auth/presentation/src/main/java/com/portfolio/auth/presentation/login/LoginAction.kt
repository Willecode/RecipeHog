package com.portfolio.auth.presentation.login

sealed interface LoginAction {
    data object OnLoginClicked: LoginAction
    data object OnRegisterClicked: LoginAction
    data class OnPasswordVisibilityChanged(val visible: Boolean) : LoginAction
}