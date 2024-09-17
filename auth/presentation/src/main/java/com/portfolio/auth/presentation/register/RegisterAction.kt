package com.portfolio.auth.presentation.register

interface RegisterAction {
    data object OnLoginClicked: RegisterAction
    data object OnRegisterClicked: RegisterAction
    data object OnEmailChanged: RegisterAction
    data object OnPasswordChanged: RegisterAction
    data object OnPasswordVisibilityChanged: RegisterAction
}