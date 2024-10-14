package com.portfolio.presentation

import com.portfolio.core.domain.model.RecipePreview

data class DiscoverState(
    val recipePreviews: List<RecipePreview> = listOf()
)
