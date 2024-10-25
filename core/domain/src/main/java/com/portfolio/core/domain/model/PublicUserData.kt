package com.portfolio.core.domain.model

import java.time.LocalDate


data class PublicUserData(
    val displayName: String,
    val creationDate: LocalDate,
    val likes: Int,
    val profilePictureUrl: String,
    val postedRecipes: List<RecipePreview>
)
