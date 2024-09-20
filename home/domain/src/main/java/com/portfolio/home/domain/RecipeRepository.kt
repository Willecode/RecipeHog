package com.portfolio.home.domain

import com.portfolio.core.domain.Recipe
import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.Result

interface RecipeRepository {
    fun getRecipe(recipeId: String): Result<Recipe, DataError.Network>
    suspend fun getRecommendedRecipes(): Result<List<Recipe>, DataError.Network>
}