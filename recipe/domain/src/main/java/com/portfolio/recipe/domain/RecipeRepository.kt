package com.portfolio.recipe.domain

import com.portfolio.core.domain.model.Recipe
import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.EmptyResult
import com.portfolio.core.domain.util.Result

interface RecipeRepository {

    suspend fun postRecipe(recipeDraft: RecipeDraft, imageFilePath: String): EmptyResult<DataError.Network>

    /**
     * Tries to fetch content from server. On failure, reads the content from the cache.
     */
    suspend fun getRecipe(recipeId: String): Result<Recipe, DataError.Network>

    /**
     * Reads content from cache.
     */
    suspend fun getRecipeFromCache(recipeId: String): Result<Recipe, DataError.Network>

    /**
     * Fetches content from the remote server.
     */
    suspend fun getRecipeFromServer(recipeId: String): Result<Recipe, DataError.Network>
}