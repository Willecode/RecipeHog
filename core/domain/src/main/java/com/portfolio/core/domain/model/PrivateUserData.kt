package com.portfolio.core.domain.model

data class PrivateUserData(
    val bookmarkedRecipes: Map<String, RecipePreview>,
    val likedRecipes: Map<String, RecipePreview>
)
