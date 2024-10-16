package com.portfolio.presentation

data class DiscoverState(
    val loadingMoreRecipes: Boolean = false,
    val loading: Boolean = true,
    val throttlingGateOpen: Boolean = true,
    val offlineMode: Boolean = false,
    val searchEnabled: Boolean = false,
    val reachedEndOfData: Boolean = false
)
