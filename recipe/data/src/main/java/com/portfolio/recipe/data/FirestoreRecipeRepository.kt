package com.portfolio.recipe.data

import com.portfolio.core.domain.model.Recipe
import com.portfolio.core.domain.model.SessionStorage
import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.EmptyResult
import com.portfolio.core.domain.util.Result
import com.portfolio.recipe.data.data_source.RecipeDataSource
import com.portfolio.recipe.domain.RecipeDraft
import com.portfolio.recipe.domain.RecipeRepository

class FirestoreRecipeRepository(
    private val recipeDataSource: RecipeDataSource,
    private val sessionStorage: SessionStorage
): RecipeRepository {

    override suspend fun getRecipeFromCache(recipeId: String): Result<Recipe, DataError.Network> {
        return recipeDataSource.getRecipeFromCache(recipeId = recipeId)
    }

    override suspend fun getRecipeFromServer(recipeId: String): Result<Recipe, DataError.Network> {
        return recipeDataSource.getRecipeFromServer(recipeId = recipeId)
    }

    override suspend fun postRecipe(
        recipeDraft: RecipeDraft,
        imageFilePath: String
    ): EmptyResult<DataError.Network> {
        val uid = sessionStorage.get()?.userId ?: return Result.Error(DataError.Network.UNAUTHORIZED)
        val username = sessionStorage.get()?.userName ?: return Result.Error(DataError.Network.UNAUTHORIZED)
        return recipeDataSource.postRecipe(
            recipeDraft = recipeDraft,
            imageFilePath = imageFilePath,
            username = username,
            userId = uid
        )
    }

    override suspend fun getRecipe(recipeId: String): Result<Recipe, DataError.Network> {
        return recipeDataSource.getRecipe(recipeId = recipeId)
    }
}