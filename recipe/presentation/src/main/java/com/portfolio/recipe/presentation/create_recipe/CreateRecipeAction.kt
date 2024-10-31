package com.portfolio.recipe.presentation.create_recipe

import android.graphics.Bitmap
import com.portfolio.recipe.presentation.create_recipe.ingredient.IngredientUnit

sealed interface CreateRecipeAction {

    data object OnBackClick: CreateRecipeAction
    data class OnPostClick(val filesDirectory: String): CreateRecipeAction

    data object OnAddEmptyIngredient: CreateRecipeAction
    data object OnAddPreparationStep: CreateRecipeAction
    data object OnAddTag: CreateRecipeAction

    data class OnDeleteIngredient(val ingredientIndex: Int): CreateRecipeAction
    data class OnDeleteTag(val tagIndex: Int): CreateRecipeAction
    data class OnDeletePreparationStep(val stepIndex: Int): CreateRecipeAction

    data class OnTitleChanged(val newTitle: String): CreateRecipeAction
    data class OnDescriptionChanged(val newDesc: String): CreateRecipeAction
    data class OnDurationChanged(val newDuration: String): CreateRecipeAction
    data class OnServingsChanged(val newServings: String): CreateRecipeAction
    data class OnIngredientUnitChanged(val ingredientIndex: Int, val unit: IngredientUnit): CreateRecipeAction
    data class OnIngredientQuantityChanged(val ingredientIndex: Int, val quantity: String): CreateRecipeAction
    data class OnIngredientItemChanged(val ingredientIndex: Int, val item: String): CreateRecipeAction
    data class OnPreparationStepChanged(val stepIndex: Int, val value: String): CreateRecipeAction
    data class OnTagChanged(val tagIndex: Int, val value: String): CreateRecipeAction

    /**
     * Permissions
     */
    data class OnCameraPermissionChanged(val hasCameraPermission: Boolean): CreateRecipeAction
    data class OnShowCameraPermRationaleChanged(val showCameraRationale: Boolean): CreateRecipeAction
    data object DismissRationaleDialog: CreateRecipeAction
    data object OnRequestCameraPermission: CreateRecipeAction

    /**
     * Camera
     */
    data object OnCameraError: CreateRecipeAction
    data class  OnPictureTaken(val picture: Bitmap): CreateRecipeAction

}
