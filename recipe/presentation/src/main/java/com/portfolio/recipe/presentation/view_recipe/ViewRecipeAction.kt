package com.portfolio.recipe.presentation.view_recipe

sealed interface ViewRecipeAction {
    data object OnLikeClicked: ViewRecipeAction
    data object OnBookmarkClicked: ViewRecipeAction
    data class OnAuthorClicked(val authorId: String): ViewRecipeAction
    data object OnBackPress: ViewRecipeAction
}