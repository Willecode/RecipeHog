package com.portfolio.home.data.model

import com.portfolio.core.domain.model.HomeContent.SingleRecipePreview
import com.portfolio.core.domain.model.RecipePreview

data class HomeContentSerializableSingle(
    val title: String = "",
    val recipeTitle: String = "",
    val author: String = "",
    val description: String = "",
    val imgUrl: String = "",
    val recipeId: String = ""
)

fun HomeContentSerializableSingle.toSingleRecipePreview(): SingleRecipePreview {
    return SingleRecipePreview(
        title = title,
        recipe = RecipePreview(
            title = recipeTitle,
            author = author,
            description = description,
            imgUrl = imgUrl,
            recipeId = recipeId
        )
    )
}