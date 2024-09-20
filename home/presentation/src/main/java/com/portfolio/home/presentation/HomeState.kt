package com.portfolio.home.presentation

import com.portfolio.core.domain.Recipe

data class HomeState(
    val recipes: List<Recipe> = listOf()
)