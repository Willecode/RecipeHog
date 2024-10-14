package com.portfolio.core.domain.model

data class RecipePreview(
    val title: String,
    val author: String,
    val description: String,
    val imgUrl: String,
    val recipeId: String
)

fun Recipe.toRecipePreview(): RecipePreview{
    return RecipePreview(
        title = this.title,
        author = this.author,
        description = this.description,
        imgUrl = this.imgUrl,
        recipeId = this.recipeId
    )
}