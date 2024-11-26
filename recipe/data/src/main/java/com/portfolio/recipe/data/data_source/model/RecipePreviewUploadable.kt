package com.portfolio.recipe.data.data_source.model

import com.portfolio.recipe.domain.RecipeDraft

data class RecipePreviewUploadable(
    val title: String = "",
    val author: String = "",
    val authorUserId: String = "",
    val description: String = "",
    val imgUrl: String = ""
)

fun RecipeDraft.toRecipePreviewUploadable(imgUrl: String, authorId: String, author: String): RecipePreviewUploadable {
    return RecipePreviewUploadable(
        title = title,
        author = author,
        authorUserId = authorId,
        description = description,
        imgUrl = imgUrl
    )
}