package com.portfolio.core.data.util

import com.portfolio.core.data.data_source.model.RecipePreviewSerializable
import com.portfolio.core.domain.model.RecipePreview

fun RecipePreviewSerializable.toRecipePreview(): RecipePreview {
    return RecipePreview(
        title = title,
        author = author,
        description = description,
        imgUrl = imgUrl,
        recipeId = recipeId
    )
}