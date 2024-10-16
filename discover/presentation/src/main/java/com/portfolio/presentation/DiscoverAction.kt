package com.portfolio.presentation

sealed interface DiscoverAction {
    data class OnSearchTextChanged(val text: String): DiscoverAction
    data object OnRecipeListEndReached: DiscoverAction
}