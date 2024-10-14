package com.portfolio.presentation

sealed interface DiscoverAction {
    data class onSearchTextChanged(val text: String): DiscoverAction
}