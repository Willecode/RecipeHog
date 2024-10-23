package com.portfolio.bookmarks.presentation

import com.portfolio.core.domain.model.RecipePreview

data class BookmarksState(
    val isLoading: Boolean = true,
    val bookmarks: List<RecipePreview> = listOf(),
    val liked: List<RecipePreview> = listOf()
)
