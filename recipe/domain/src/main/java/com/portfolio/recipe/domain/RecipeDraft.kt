package com.portfolio.recipe.domain

import com.portfolio.core.domain.model.IngredientListing

data class RecipeDraft(
    val title: String,
    val description: String,
    val duration: Int,
    val servings: Int,
    val ingredientDrafts: List<IngredientListing>,
    val preparationSteps: List<String>,
    val tags: List<String>
)


