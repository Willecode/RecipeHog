package com.portfolio.discover.data.data_source

import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.EmptyResult
import com.portfolio.domain.PaginatedRecipePreviewList
import kotlinx.coroutines.flow.Flow

/**
 * Data source that fulfills the purpose of both local and remote data source.
 * Intended for solutions that do not make total separation of remote and local functionality
 * possible - such as Firestore.
 */
interface DiscoverDataSource {
    fun getRecipes(): Flow<PaginatedRecipePreviewList>
    suspend fun loadInitialRecipesFromCache(limit: Int, titleQuery: String): EmptyResult<DataError>
    suspend fun loadMoreRecipesFromCache(limit: Int, titleQuery: String): EmptyResult<DataError>
    suspend fun fetchInitialRecipesFromServer(limit: Int, titleQuery: String): EmptyResult<DataError.Network>
    suspend fun fetchMoreRecipesFromServer(limit: Int, titleQuery: String): EmptyResult<DataError.Network>
}