package com.portfolio.core.data.data_source.model

import com.portfolio.core.domain.model.Recipe

data class RecipeSerializable(
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
    val ingredients: List<IngredientListingSerializable> = listOf()
)

fun RecipeSerializable.toRecipePreviewSerializable(): RecipePreviewSerializable {
    return RecipePreviewSerializable(
        title = this.title,
        author = this.author,
        description = this.description,
        imgUrl = this.imgUrl,
        recipeId = this.recipeId
    )
}

fun RecipeSerializable.toRecipe(): Recipe {
    return Recipe(
        title = title,
        author = author,
        authorUserId = authorUserId,
        description = description,
        imgUrl = imgUrl,
        recipeId = recipeId,
        likeCount = likeCount,
        durationMinutes = durationMinutes,
        servings = servings,
        tags = tags,
        instructions = instructions,
        ingredients = ingredients.map { it.toIngredientListing() }
    )
}