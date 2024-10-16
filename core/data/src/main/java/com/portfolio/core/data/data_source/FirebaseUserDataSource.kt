package com.portfolio.core.data.data_source

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Source
import com.portfolio.core.data.FirebaseConstants.RECIPES_COLLECTION
import com.portfolio.core.data.FirebaseConstants.USER_BOOKMARKED_RECIPES_DOCUMENT
import com.portfolio.core.data.FirebaseConstants.USER_COLLECTION
import com.portfolio.core.data.FirebaseConstants.USER_LIKED_RECIPES_DOCUMENT
import com.portfolio.core.data.FirebaseConstants.USER_PRIVATE_DATA_BOOKMARKED_RECIPES_CONTENT_FIELD
import com.portfolio.core.data.FirebaseConstants.USER_PRIVATE_DATA_COLLECTION
import com.portfolio.core.data.FirebaseConstants.USER_PRIVATE_DATA_LIKED_RECIPES_CONTENT_FIELD
import com.portfolio.core.data.data_source.util.PublicUserDataSerializable
import com.portfolio.core.data.data_source.util.toPrivateUserData
import com.portfolio.core.data.data_source.util.toPublicUserData
import com.portfolio.core.domain.model.Recipe
import com.portfolio.core.domain.model.RecipePreview
import com.portfolio.core.domain.model.UserData
import com.portfolio.core.domain.model.toRecipePreview
import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.EmptyResult
import com.portfolio.core.domain.util.Result
import com.portfolio.core.domain.util.asEmptyDataResult
import kotlinx.coroutines.tasks.await

class FirebaseUserDataSource(
    private val firestore: FirebaseFirestore
) : UserDataSource {
    override suspend fun getUserData(userId: String, includePrivateData: Boolean): Result<UserData, DataError.Network> {
        return getUserData(userId = userId, source = Source.DEFAULT, includePrivateData = includePrivateData)
    }

    override suspend fun getUserDataFromCache(userId: String, includePrivateData: Boolean): Result<UserData, DataError.Network> {
        return getUserData(userId = userId, source = Source.CACHE, includePrivateData = includePrivateData)
    }

    override suspend fun getUserDataFromServer(userId: String, includePrivateData: Boolean): Result<UserData, DataError.Network> {
        return getUserData(userId = userId, source = Source.SERVER, includePrivateData = includePrivateData)
    }

    override suspend fun likeRecipe(userId: String, recipeId: String): EmptyResult<DataError.Network> {
        return safeCall{
            val snapshot = firestore
                .collection(RECIPES_COLLECTION)
                .document(recipeId)
                .get(Source.CACHE)
                .await()

            if (snapshot == null) {
                throw (FirebaseFirestoreException(
                    "Failed to get recipe document",
                    FirebaseFirestoreException.Code.UNKNOWN
                ))
            }

            val recipe = snapshot.toObject(Recipe::class.java)!!
            recipe.recipeId = recipeId

            return@safeCall likeRecipe(userId = userId, recipe = recipe.toRecipePreview())
        }
    }

    override suspend fun unlikeRecipe(userId: String, recipeId: String): EmptyResult<DataError.Network> {
        return safeCall {
            val likedRecipesRef = firestore
                .collection(USER_COLLECTION)
                .document(userId)
                .collection(USER_PRIVATE_DATA_COLLECTION)
                .document(USER_LIKED_RECIPES_DOCUMENT)

            val updates = hashMapOf<String, Any>(
                "${USER_PRIVATE_DATA_LIKED_RECIPES_CONTENT_FIELD}.${recipeId}" to FieldValue.delete()
            )

            likedRecipesRef.update(updates).await()

            return@safeCall Result.Success(Unit).asEmptyDataResult()
        }
    }

    override suspend fun bookmarkRecipe(userId: String, recipeId: String): EmptyResult<DataError.Network> {
        return safeCall{
            val snapshot = firestore
                .collection(RECIPES_COLLECTION)
                .document(recipeId)
                .get(Source.CACHE)
                .await()

            if (snapshot == null) {
                throw (FirebaseFirestoreException(
                    "Failed to get recipe document",
                    FirebaseFirestoreException.Code.UNKNOWN
                ))
            }

            val recipe = snapshot.toObject(Recipe::class.java)!!
            recipe.recipeId = recipeId

            return@safeCall bookmarkRecipe(userId = userId, recipe = recipe.toRecipePreview())
        }
    }

    override suspend fun unbookmarkRecipe(userId: String, recipeId: String): EmptyResult<DataError.Network> {
        return safeCall {
            val likedRecipesRef = firestore
                .collection(USER_COLLECTION)
                .document(userId)
                .collection(USER_PRIVATE_DATA_COLLECTION)
                .document(USER_BOOKMARKED_RECIPES_DOCUMENT)

            val updates = hashMapOf<String, Any>(
                "${USER_PRIVATE_DATA_BOOKMARKED_RECIPES_CONTENT_FIELD}.${recipeId}" to FieldValue.delete()
            )

            likedRecipesRef.update(updates).await()

            return@safeCall Result.Success(Unit).asEmptyDataResult()
        }
    }

    private suspend fun bookmarkRecipe(userId: String, recipe: RecipePreview): EmptyResult<DataError.Network> {
        return safeCall{
            val savedRecipesRef = firestore
                .collection(USER_COLLECTION)
                .document(userId)
                .collection(USER_PRIVATE_DATA_COLLECTION)
                .document(USER_BOOKMARKED_RECIPES_DOCUMENT)


            try {
                savedRecipesRef.update(
                    /* field = */ "${USER_PRIVATE_DATA_BOOKMARKED_RECIPES_CONTENT_FIELD}.${recipe.recipeId}",
                    /* value = */ recipe
                ).await()
            } catch (e: FirebaseFirestoreException) {
                when (e.code) {
                    FirebaseFirestoreException.Code.NOT_FOUND -> {
                        // Doc didn't exist, create it instead
                        val data = mapOf(
                            "${USER_PRIVATE_DATA_BOOKMARKED_RECIPES_CONTENT_FIELD}.${recipe.recipeId}" to recipe
                        )
                        savedRecipesRef.set(data)
                    }
                    else -> throw e
                }
            }

            return Result.Success(Unit).asEmptyDataResult()
        }
    }

    private suspend fun likeRecipe(userId: String, recipe: RecipePreview): EmptyResult<DataError.Network> {
        return safeCall{
            val savedRecipesRef = firestore
                .collection(USER_COLLECTION)
                .document(userId)
                .collection(USER_PRIVATE_DATA_COLLECTION)
                .document(USER_LIKED_RECIPES_DOCUMENT)

            try {
                val result = savedRecipesRef.update(
                    /* field = */ "${USER_PRIVATE_DATA_LIKED_RECIPES_CONTENT_FIELD}.${recipe.recipeId}",
                    /* value = */ recipe
                ).await()
            } catch (e: FirebaseFirestoreException) {
                when (e.code) {
                    FirebaseFirestoreException.Code.NOT_FOUND -> {
                        // Doc didn't exist, create it instead
                        val data = mapOf(
                            "${USER_PRIVATE_DATA_LIKED_RECIPES_CONTENT_FIELD}.${recipe.recipeId}" to recipe
                        )
                        savedRecipesRef.set(data)
                    }
                    else -> throw e
                }
            }

            return Result.Success(Unit).asEmptyDataResult()
        }
    }

    private suspend fun getUserData(userId: String, source: Source, includePrivateData: Boolean): Result<UserData, DataError.Network> {
        return safeCall {
            val publicDataRef = firestore
                .collection(USER_COLLECTION)
                .document(userId)

            val privateDataRef = firestore
                .collection(USER_COLLECTION)
                .document(userId)
                .collection(USER_PRIVATE_DATA_COLLECTION)

            val taskPublicData = publicDataRef.get(source)

            val taskPrivateData = if (includePrivateData) {
                privateDataRef.get()
            } else null

            val snapshotPublicData = taskPublicData.await()
            val snapshotPrivateData = taskPrivateData?.await()

            val publicData = snapshotPublicData?.toObject(PublicUserDataSerializable::class.java)?.toPublicUserData()
                ?: throw FirebaseFirestoreException("Failed to get public user data", FirebaseFirestoreException.Code.UNKNOWN)

            val privateData = snapshotPrivateData?.let{
                it.toPrivateUserData()
            }

            val userData = UserData(
                publicUserData = publicData,
                privateUserData = privateData
            )

            return Result.Success(userData)
        }
    }

    private suspend inline fun <reified T> safeCall(function: suspend () -> Result<T, DataError.Network>): Result<T, DataError.Network> {
        return try {
            function()
        } catch (e: FirebaseFirestoreException) {
            return when (e.code) {
                FirebaseFirestoreException.Code.UNAVAILABLE -> Result.Error(DataError.Network.NO_INTERNET)
                FirebaseFirestoreException.Code.UNAUTHENTICATED -> Result.Error(DataError.Network.UNAUTHORIZED)
                else -> Result.Error(DataError.Network.UNKNOWN)
            }
        }
    }
}