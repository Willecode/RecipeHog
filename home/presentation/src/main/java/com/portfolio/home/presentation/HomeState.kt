package com.portfolio.home.presentation

import com.portfolio.core.domain.model.HomeContent

data class HomeState(
    val userName: String? = null,
    val contentList: List<HomeContent> = listOf(),
    val isLoading: Boolean = false,
    val isFetchingContent: Boolean = false
)