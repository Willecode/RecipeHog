package com.portfolio.recipe.presentation.create_recipe.ingredient

data class IngredientDraft(
    val ingredient: IngredientUI,
    val showQuantityError: Boolean,
    val showNameError: Boolean
)
