package com.portfolio.recipe.data

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Source
import com.google.firebase.storage.FirebaseStorage
import com.portfolio.core.data.FirebaseConstants.RECIPES_COLLECTION
import com.portfolio.core.data.FirebaseConstants.RECIPE_PREVIEWS_COLLECTION
import com.portfolio.core.domain.model.IngredientListing
import com.portfolio.core.domain.model.Recipe
import com.portfolio.core.domain.model.SessionStorage
import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.EmptyResult
import com.portfolio.core.domain.util.Result
import com.portfolio.core.domain.util.asEmptyDataResult
import com.portfolio.recipe.domain.RecipeDraft
import com.portfolio.recipe.domain.RecipeRepository
import kotlinx.coroutines.tasks.await
import java.io.File

/**
 * TODO: Add a layer of abstraction such as in User Data repo for better decoupling
 */
class FirestoreRecipeRepository(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val sessionStorage: SessionStorage
): RecipeRepository {

    private suspend fun getRecipe(recipeId: String, source: Source): Result<Recipe, DataError.Network> {
        try {
            val snapshot = firestore.collection("recipes")
                .document(recipeId)
                .get(source)
                .await()

            if (snapshot == null) {
                throw(FirebaseFirestoreException("Failed to get recipe document", FirebaseFirestoreException.Code.UNKNOWN))
            }

            val recipe = snapshot.toObject(Recipe::class.java)!!

            return Result.Success(recipe)

        } catch (e: FirebaseFirestoreException) {
            return when (e.code) {
                FirebaseFirestoreException.Code.UNAVAILABLE -> Result.Error(DataError.Network.NO_INTERNET)
                FirebaseFirestoreException.Code.UNAUTHENTICATED -> Result.Error(DataError.Network.UNAUTHORIZED)
                else -> Result.Error(DataError.Network.UNKNOWN)

            }
        }
    }

    override suspend fun getRecipeFromCache(recipeId: String): Result<Recipe, DataError.Network> {
        return getRecipe(recipeId = recipeId, source = Source.CACHE)
    }

    override suspend fun getRecipeFromServer(recipeId: String): Result<Recipe, DataError.Network> {
        return getRecipe(recipeId = recipeId, source = Source.SERVER)
    }

    // TODO: Add error handling. Storage upload hangs forever if no connection.
    override suspend fun postRecipe(
        recipeDraft: RecipeDraft,
        imageFilePath: String
    ): EmptyResult<DataError.Network> {
        val uid = sessionStorage.get()?.userId ?: return Result.Error(DataError.Network.UNAUTHORIZED)
        val username = sessionStorage.get()?.userName ?: return Result.Error(DataError.Network.UNAUTHORIZED)

        val file = Uri.fromFile(File(imageFilePath))
        val imageRef = storage.reference.child("recipe_images/${file.lastPathSegment}")
        val uploadTask = imageRef.putFile(file)

        uploadTask.await()
            ?: throw(FirebaseFirestoreException("Failed to post recipe", FirebaseFirestoreException.Code.UNKNOWN))

        val imageUrlSnapshot = imageRef.downloadUrl.await()
            ?: throw(FirebaseFirestoreException("Failed to post recipe", FirebaseFirestoreException.Code.UNKNOWN))

        val imgUrl = imageUrlSnapshot.toString()

        val recipesRef = firestore
            .collection(RECIPES_COLLECTION)

        val docRef = recipesRef.add(
            recipeDraft.toRecipeUploadable(
                imgUrl = imgUrl,
                authorId = uid,
                author = username
            )
        ).await()

        // Cloud functions not available, so need to manually upload the preview version.
        val recipeId = docRef.id
        firestore
            .collection(RECIPE_PREVIEWS_COLLECTION)
            .document(recipeId)
            .set(
                recipeDraft.toRecipePreviewUploadable(
                    imgUrl = imgUrl,
                    authorId = uid,
                    author = username
                )
        ).await()

        return Result.Success(Unit).asEmptyDataResult()
    }

    override suspend fun getRecipe(recipeId: String): Result<Recipe, DataError.Network> {
        return getRecipe(recipeId = recipeId, source = Source.DEFAULT)
    }

    data class RecipeUploadable(
        val title: String = "",
        val author: String = "",
        val authorUserId: String = "",
        val description: String = "",
        val imgUrl: String = "",
        val likeCount: Int = 0,
        val durationMinutes: Int = 0,
        val servings: Int = 0,
        val tags: List<String> = listOf(),
        val instructions: List<String> = listOf(),
        val ingredients: List<IngredientListing> = listOf()
    )

    data class RecipePreviewUploadable(
        val title: String = "",
        val author: String = "",
        val authorUserId: String = "",
        val description: String = "",
        val imgUrl: String = ""
    )

    private fun RecipeDraft.toRecipeUploadable(imgUrl: String, authorId: String, author: String): RecipeUploadable {
        return RecipeUploadable(
            title = title,
            author = author,
            authorUserId = authorId,
            description = description,
            imgUrl = imgUrl,
            likeCount = 0,
            durationMinutes = duration,
            servings = servings,
            instructions = preparationSteps,
            ingredients = ingredientDrafts
        )
    }

    private fun RecipeDraft.toRecipePreviewUploadable(imgUrl: String, authorId: String, author: String): RecipePreviewUploadable {
        return RecipePreviewUploadable(
            title = title,
            author = author,
            authorUserId = authorId,
            description = description,
            imgUrl = imgUrl
        )
    }
}