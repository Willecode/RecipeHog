package com.portfolio.home.presentation

sealed interface HomeAction {
    data object OnSearchClick: HomeAction
    data class OnRecipeClick(val recipeId: String): HomeAction
    data object OnLogoutClick: HomeAction
}