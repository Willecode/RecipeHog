package com.portfolio.data

import com.portfolio.core.domain.model.RecipePreview
import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.EmptyResult
import com.portfolio.data.data_source.DiscoverDataSource
import com.portfolio.domain.DiscoverRepository
import kotlinx.coroutines.flow.Flow

class DefaultDiscoverRepository(
    private val dataSource: DiscoverDataSource
): DiscoverRepository{


    override fun getRecipes(): Flow<List<RecipePreview>> = dataSource.getRecipes()

    override suspend fun fetchRecipesStart(limit: Int, titleQuery: String): EmptyResult<DataError.Network> =
        dataSource.fetchRecipesStart(limit, titleQuery)

    override suspend fun fetchRecipesContinue(limit: Int, titleQuery: String): EmptyResult<DataError.Network> =
        dataSource.fetchRecipesContinue(limit, titleQuery)

}