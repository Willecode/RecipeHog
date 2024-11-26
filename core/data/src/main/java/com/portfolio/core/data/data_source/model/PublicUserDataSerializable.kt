package com.portfolio.core.data.data_source.model

import com.portfolio.core.data.util.toRecipePreview
import com.portfolio.core.domain.model.PublicUserData
import java.time.ZoneId
import java.util.Date

data class PublicUserDataSerializable(
    val displayName: String = "",
    val creationDate: Date = Date(),
    val likes: Int = 0,
    val profilePictureUrl: String = "",
    val postedRecipes: Map<String, RecipePreviewSerializable> = mapOf()
)
fun PublicUserDataSerializable.toPublicUserData(): PublicUserData {
    return PublicUserData(
        displayName = displayName,
        creationDate = creationDate
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        ,
        likes = likes,
        profilePictureUrl = profilePictureUrl,
        postedRecipes = postedRecipes.toList().map {
            var preview = it.second.toRecipePreview()
            preview = preview.copy(recipeId = it.first)
            preview
        }
    )
}