package com.portfolio.recipe.data.data_source

import com.portfolio.core.domain.model.Recipe
import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.EmptyResult
import com.portfolio.core.domain.util.Result
import com.portfolio.recipe.domain.RecipeDraft

interface RecipeDataSource {
    suspend fun getRecipeFromCache(recipeId: String): Result<Recipe, DataError>
    suspend fun getRecipeFromServer(recipeId: String): Result<Recipe, DataError.Network>
    suspend fun postRecipe(
        recipeDraft: RecipeDraft,
        imageFilePath: String,
        username: String,
        userId: String
    ): EmptyResult<DataError.Network>
}