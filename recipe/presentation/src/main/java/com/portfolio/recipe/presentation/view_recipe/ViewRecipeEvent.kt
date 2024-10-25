package com.portfolio.recipe.presentation.view_recipe

import com.portfolio.core.presentation.ui.UiText

sealed interface ViewRecipeEvent {
    data class ViewRecipeError(val error: UiText): ViewRecipeEvent
    data object AuthError: ViewRecipeEvent
    data class OnReviewsClicked(val recipeId: String): ViewRecipeEvent
}