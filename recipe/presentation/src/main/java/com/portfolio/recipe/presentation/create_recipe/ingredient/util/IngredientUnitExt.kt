package com.portfolio.recipe.presentation.create_recipe.ingredient.util

import com.portfolio.core.presentation.ui.UiText
import com.portfolio.recipe.presentation.R
import com.portfolio.recipe.presentation.create_recipe.ingredient.IngredientUnit


fun IngredientUnit.toUiText(): UiText {
    return when (this) {
        IngredientUnit.Clove -> UiText.StringResource(R.string.clove)
        IngredientUnit.Cup -> UiText.StringResource(R.string.cup)
        IngredientUnit.Gram -> UiText.StringResource(R.string.gram)
        IngredientUnit.Handful -> UiText.StringResource(R.string.handful)
        IngredientUnit.Head -> UiText.StringResource(R.string.head)
        IngredientUnit.Piece -> UiText.StringResource(R.string.piece)
        IngredientUnit.TableSpoon -> UiText.StringResource(R.string.tablespoon)
        IngredientUnit.TeaSpoon -> UiText.StringResource(R.string.teaspoon)
        IngredientUnit.ToTaste -> UiText.StringResource(R.string.to_taste)
    }
}

fun IngredientUnit.asString(): String {
    return when (this) {
        IngredientUnit.Clove ->  "clove"
        IngredientUnit.Cup ->  "cup"
        IngredientUnit.Gram ->  "g"
        IngredientUnit.Handful ->  "handful"
        IngredientUnit.Head ->  "head"
        IngredientUnit.Piece ->  "piece"
        IngredientUnit.TableSpoon -> "tbsp"
        IngredientUnit.TeaSpoon ->  "ts"
        IngredientUnit.ToTaste ->  "to taste"
    }
}