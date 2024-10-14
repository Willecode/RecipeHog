package com.portfolio.recipe.presentation.create_recipe.ingredient

sealed class IngredientUI(
    open val name: String,
    open val unit: IngredientUnit
) {
    data class QuantityIngredient(
        override val name: String = "",
        val quantity: String = "",
        override val unit: IngredientUnit = IngredientUnit.Gram
    ) : IngredientUI(name, unit)

    data class NoQuantityIngredient(
        override val name: String = "",
        override val unit: IngredientUnit = IngredientUnit.ToTaste
    ) : IngredientUI(name, unit)
}
