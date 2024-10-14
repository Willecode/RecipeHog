package com.portfolio.core.domain.model

sealed interface HomeContent {
    data class MultiRecipePreview(val title: String, val recipes: List<RecipePreview>): HomeContent
    data class SingleRecipePreview(val title: String, val recipe: RecipePreview): HomeContent
}
