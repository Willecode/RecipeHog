package com.portfolio.recipe.presentation.create_recipe

import com.portfolio.core.presentation.ui.UiText

sealed interface CreateRecipeEvent {
    data class Error(val error: UiText): CreateRecipeEvent
    data object RecipePostSuccessful: CreateRecipeEvent
}