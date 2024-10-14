package com.portfolio.core.data.data_source.util

import com.portfolio.core.domain.model.PublicUserData
import com.portfolio.core.domain.model.RecipePreview
import java.util.Date

data class PublicUserDataSerializable(
    val displayName: String = "",
    val creationDate: Date = Date(),
    val likes: Int = 0,
    val profilePictureUrl: String = "",
    val postedRecipes: Map<String, RecipePreview> = mapOf()
)
fun PublicUserDataSerializable.toPublicUserData(): PublicUserData {
    return PublicUserData(
        displayName = displayName,
        creationDate = creationDate,
        likes = likes,
        profilePictureUrl = profilePictureUrl,
        postedRecipes = postedRecipes
    )
}