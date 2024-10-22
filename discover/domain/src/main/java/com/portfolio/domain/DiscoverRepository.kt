package com.portfolio.domain

import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow

interface DiscoverRepository {
    fun getRecipes(): Flow<PaginatedRecipePreviewList>
    suspend fun loadInitialRecipesFromCache(limit: Int, titleQuery: String): EmptyResult<DataError>
    suspend fun loadMoreRecipesFromCache(limit: Int, titleQuery: String): EmptyResult<DataError>
    suspend fun fetchInitialRecipesFromServer(limit: Int, titleQuery: String): EmptyResult<DataError.Network>
    suspend fun fetchMoreRecipesFromServer(limit: Int, titleQuery: String): EmptyResult<DataError.Network>
}