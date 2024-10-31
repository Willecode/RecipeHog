package com.portfolio.recipe.presentation.create_recipe

import android.graphics.Bitmap
import com.portfolio.recipe.presentation.create_recipe.ingredient.IngredientDraft
import com.portfolio.recipe.presentation.create_recipe.ingredient.IngredientUI
import com.portfolio.recipe.presentation.create_recipe.preparation.PreparationStep
import com.portfolio.recipe.presentation.create_recipe.tag.TagDraft

data class CreateRecipeState(
    val title: String = "",
    val showTitleError: Boolean = false,
    val description: String = "",
    val showDescriptionError: Boolean = false,
    val duration: String = "",
    val showDurationError: Boolean = false,
    val servings: String = "",
    val showServingsError: Boolean = false,
    val ingredientDrafts: List<IngredientDraft> = listOf(IngredientDraft(
        ingredient = IngredientUI.QuantityIngredient(),
        showQuantityError = false,
        showNameError = false
    )),
    val preparationSteps: List<PreparationStep> = listOf(
        PreparationStep(
            text = "",
            showError = false
        )
    ),
    val tags: List<TagDraft> = listOf(),
    val picture: Bitmap? = null,
    /**
     * Permissions
     */
    val showCameraPermissionRationale: Boolean = false,
    val hasCameraPermission: Boolean = false,
    /**
     * Status
     */
    val posting: Boolean = false
)