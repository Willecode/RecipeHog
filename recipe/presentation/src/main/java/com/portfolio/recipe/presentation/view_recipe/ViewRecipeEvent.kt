package com.portfolio.recipe.presentation.view_recipe

import com.portfolio.core.presentation.ui.UiText

sealed interface ViewRecipeEvent {
    data class ViewRecipeError(val error: UiText): ViewRecipeEvent
}