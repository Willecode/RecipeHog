package com.portfolio.core.domain.model

import java.util.Date


data class PublicUserData(
    val displayName: String,
    val creationDate: Date,
    val likes: Int,
    val profilePictureUrl: String,
    val postedRecipes: Map<String, RecipePreview>
)
