package com.portfolio.auth.presentation.intro

sealed interface IntroAction {
    data object OnLoginClicked: IntroAction
    data object OnRegisterClicked: IntroAction
}