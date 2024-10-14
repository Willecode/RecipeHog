package com.portfolio.core.domain.model

data class Recipe(
    val title: String = "",
    val author: String = "",
    val authorUserId: String = "",
    val description: String = "",
    val imgUrl: String = "",
    var recipeId: String = "",
    val likeCount: Int = 0,
    val durationMinutes: Int = 0,
    val servings: Int = 0,
    val tags: List<String> = listOf(),
    val instructions: List<String> = listOf(),
    val ingredients: List<IngredientListing> = listOf()
)


