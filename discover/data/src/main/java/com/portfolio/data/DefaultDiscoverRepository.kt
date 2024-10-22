package com.portfolio.data

import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.EmptyResult
import com.portfolio.data.data_source.DiscoverDataSource
import com.portfolio.domain.DiscoverRepository
import com.portfolio.domain.PaginatedRecipePreviewList
import kotlinx.coroutines.flow.Flow

class DefaultDiscoverRepository(
    private val dataSource: DiscoverDataSource
): DiscoverRepository{


    override fun getRecipes(): Flow<PaginatedRecipePreviewList> = dataSource.getRecipes()
    override suspend fun loadInitialRecipesFromCache(
        limit: Int,
        titleQuery: String
    ): EmptyResult<DataError> {
        return dataSource.loadInitialRecipesFromCache(limit = limit, titleQuery = titleQuery)
    }

    override suspend fun loadMoreRecipesFromCache(
        limit: Int,
        titleQuery: String
    ): EmptyResult<DataError> {
        return dataSource.loadMoreRecipesFromCache(limit = limit, titleQuery = titleQuery)
    }

    override suspend fun fetchInitialRecipesFromServer(
        limit: Int,
        titleQuery: String
    ): EmptyResult<DataError.Network> {
        return dataSource.fetchInitialRecipesFromServer(limit = limit, titleQuery = titleQuery)
    }

    override suspend fun fetchMoreRecipesFromServer(
        limit: Int,
        titleQuery: String
    ): EmptyResult<DataError.Network> {
        return dataSource.fetchMoreRecipesFromServer(limit = limit, titleQuery = titleQuery)
    }


}