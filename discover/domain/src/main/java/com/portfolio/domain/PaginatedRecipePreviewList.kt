package com.portfolio.domain

import com.portfolio.core.domain.model.RecipePreview

data class PaginatedRecipePreviewList (
    val recipes: List<RecipePreview>,
    val reachedEndOfData: Boolean
)