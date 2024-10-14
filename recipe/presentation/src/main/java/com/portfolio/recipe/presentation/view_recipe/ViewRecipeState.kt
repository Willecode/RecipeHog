package com.portfolio.recipe.presentation.view_recipe

import com.portfolio.core.domain.model.Recipe

data class ViewRecipeState (
    val recipe: Recipe? = null,
    val isLoading: Boolean = true,
    val cantGetRecipe: Boolean = false,
    val isRecipeLiked: Boolean = false,
    val isRecipeBookmarked: Boolean = false
)