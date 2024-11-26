package com.portfolio.recipe.data.data_source.model

import com.portfolio.core.data.data_source.model.IngredientListingSerializable
import com.portfolio.core.data.data_source.model.toIngredientListingSerializable
import com.portfolio.recipe.domain.RecipeDraft

data class RecipeUploadable(
    val title: String = "",
    val author: String = "",
    val authorUserId: String = "",
    val description: String = "",
    val imgUrl: String = "",
    val likeCount: Int = 0,
    val durationMinutes: Int = 0,
    val servings: Int = 0,
    val tags: List<String> = listOf(),
    val instructions: List<String> = listOf(),
    val ingredients: List<IngredientListingSerializable> = listOf()
)

fun RecipeDraft.toRecipeUploadable(imgUrl: String, authorId: String, author: String): RecipeUploadable {
    return RecipeUploadable(
        title = title,
        author = author,
        authorUserId = authorId,
        description = description,
        imgUrl = imgUrl,
        likeCount = 0,
        durationMinutes = duration,
        servings = servings,
        instructions = preparationSteps,
        ingredients = ingredientDrafts.map { it.toIngredientListingSerializable() },
        tags = tags
    )
}