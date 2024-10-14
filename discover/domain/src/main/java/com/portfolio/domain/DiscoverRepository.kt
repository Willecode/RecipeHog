package com.portfolio.domain

import com.portfolio.core.domain.model.RecipePreview
import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow

interface DiscoverRepository {
    fun getRecipes(): Flow<List<RecipePreview>>
    suspend fun fetchRecipesStart(limit: Int, titleQuery: String): EmptyResult<DataError.Network>
    suspend fun fetchRecipesContinue(limit: Int, titleQuery: String): EmptyResult<DataError.Network>
}