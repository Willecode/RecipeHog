package com.portfolio.home.data

import com.google.firebase.firestore.FirebaseFirestore
import com.portfolio.core.domain.Recipe
import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.Result
import com.portfolio.home.domain.RecipeRepository
import kotlinx.coroutines.tasks.await

class FirebaseRecipeRepository(
    private val db: FirebaseFirestore
): RecipeRepository {
    override fun getRecipe(recipeId: String): Result<Recipe, DataError.Network> {
        return Result.Error(DataError.Network.UNKNOWN)
    }

    override suspend fun getRecommendedRecipes(): Result<List<Recipe>, DataError.Network> {
        val result = db.collection("Recipes")
            .get()
            .await()

        result.map {
            Recipe(title = it.data["title"].toString(), description = it.data["description"].toString())
        }

        return Result.Success(
            result.map {
                Recipe(title = it.data["title"].toString(), description = it.data["description"].toString())
            }
        )
    }
}