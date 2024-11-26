package com.portfolio.home.data.model

import com.portfolio.core.domain.model.HomeContent.MultiRecipePreview
import com.portfolio.core.domain.model.RecipePreview

data class HomeContentSerializableMulti(
    val title: String = "",
    val content: List<Map<String, String>> = listOf()
)

fun HomeContentSerializableMulti.toMultiRecipePreview(): MultiRecipePreview {
    return MultiRecipePreview(
        title = title,
        recipes = content.map {
            RecipePreview(
                title = it["title"] ?: "",
                author = it["author"] ?: "",
                description = it["description"] ?: "",
                imgUrl = it["imgUrl"] ?: "",
                recipeId = it["recipeId"] ?: ""
            )
        }
    )
}