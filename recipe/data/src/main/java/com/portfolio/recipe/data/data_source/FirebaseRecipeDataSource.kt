package com.portfolio.recipe.data.data_source

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Source
import com.portfolio.core.data.FirebaseConstants.RECIPES_COLLECTION
import com.portfolio.core.data.FirebaseConstants.RECIPE_PREVIEWS_COLLECTION
import com.portfolio.core.data.FirebaseConstants.USER_COLLECTION
import com.portfolio.core.data.FirebaseConstants.USER_POSTED_RECIPES_FIELD
import com.portfolio.core.data.data_source.model.RecipeSerializable
import com.portfolio.core.data.data_source.model.toRecipe
import com.portfolio.core.data.util.FirebaseStorageUploader
import com.portfolio.core.data.util.firestoreSafeCallCache
import com.portfolio.core.data.util.firestoreSafeCallServer
import com.portfolio.core.domain.model.Recipe
import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.EmptyResult
import com.portfolio.core.domain.util.Result
import com.portfolio.core.domain.util.asEmptyDataResult
import com.portfolio.recipe.data.data_source.model.RecipePreviewUploadable
import com.portfolio.recipe.data.data_source.model.toRecipePreviewUploadable
import com.portfolio.recipe.data.data_source.model.toRecipeUploadable
import com.portfolio.recipe.domain.RecipeDraft
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseRecipeDataSource(
    private val firestore: FirebaseFirestore,
    private val firebaseStorageUploader: FirebaseStorageUploader
): RecipeDataSource {
    override suspend fun getRecipeFromCache(recipeId: String): Result<Recipe, DataError> {
        return firestoreSafeCallCache {
            getRecipe(recipeId = recipeId, source = Source.CACHE)
        }
    }

    override suspend fun getRecipeFromServer(recipeId: String): Result<Recipe, DataError.Network> {
        return firestoreSafeCallServer {
            getRecipe(recipeId = recipeId, source = Source.SERVER)
        }
    }

    /**
     * This function suffers significantly from the lack of cloud functions in the backend.
     * Without CF it has to post the recipe to many collections instead of just one, and is error prone.
     */
    override suspend fun postRecipe(
        recipeDraft: RecipeDraft,
        imageFilePath: String,
        username: String,
        userId: String
    ): EmptyResult<DataError.Network> {
        return firestoreSafeCallServer {
            val storageUploadPath = generateStorageUploadPath()
            val imgUrl = firebaseStorageUploader.tryUploadImage(imageFilePath, storageUploadPath)
            try {
                uploadRecipeAndPreview(recipeDraft, imgUrl, userId, username)
            } catch (e: FirebaseFirestoreException) {
                firebaseStorageUploader.scheduleUploadedFileDeletion(storageUploadPath)
                throw e
            }

            return Result.Success(Unit).asEmptyDataResult()
        }
    }

    private suspend fun FirebaseRecipeDataSource.uploadRecipeAndPreview(
        recipeDraft: RecipeDraft,
        imgUrl: String,
        userId: String,
        username: String
    ) {
        val docRef = uploadRecipe(recipeDraft, imgUrl, userId, username)

        // Cloud functions not available, so need to manually upload the preview version.
        val recipeId = docRef.id
        try {
            val recipePreviewUploadable = recipeDraft.toRecipePreviewUploadable(
                imgUrl = imgUrl,
                authorId = userId,
                author = username
            )

            uploadRecipePreview(
                recipeId = recipeId,
                recipePreviewUploadable = recipePreviewUploadable
            )

        } catch (e: FirebaseFirestoreException) {
            // If preview upload failed, delete the recipe.
            firestore.collection(RECIPES_COLLECTION).document(recipeId)
                .delete().await()
            throw e
        }
    }

    private suspend fun uploadRecipe(
        recipeDraft: RecipeDraft,
        imgUrl: String,
        userId: String,
        username: String
    ): DocumentReference =
        firestore.collection(RECIPES_COLLECTION).add(
            recipeDraft.toRecipeUploadable(imgUrl = imgUrl, authorId = userId, author = username)
        ).await()

    private suspend fun uploadRecipePreview(
        recipeId: String,
        recipePreviewUploadable: RecipePreviewUploadable
    ) {
        // Upload to RECIPE_PREVIEWS_COLLECTION
        firestore
            .collection(RECIPE_PREVIEWS_COLLECTION)
            .document(recipeId)
            .set(
                recipePreviewUploadable
            ).await()

        // Upload to USER_COLLECTION.USER_POSTED_RECIPES_FIELD
        try {
            firestore
                .collection(USER_COLLECTION)
                .document(recipePreviewUploadable.authorUserId)
                .update(
                    /* field = */ "${USER_POSTED_RECIPES_FIELD}.${recipeId}",
                    /* value = */ recipePreviewUploadable
                ).await()
        } catch (e: FirebaseFirestoreException) {
            // If preview upload failed, delete the recipe.
            firestore.collection(RECIPE_PREVIEWS_COLLECTION).document(recipeId)
                .delete().await()
            throw e
        }
    }

    /**
     * Returns the path that the file will have in the remote database
     */
    private fun generateStorageUploadPath() = "$RECIPE_IMAGES_FOLDER/${generateRandomId()}.jpg)"

    private fun generateRandomId() = UUID.randomUUID().toString()

    private suspend fun getRecipe(recipeId: String, source: Source): Result<Recipe, DataError.Network> {
            val snapshot = firestore.collection("recipes")
                .document(recipeId)
                .get(source)
                .await()

            if (snapshot == null) {
                throw(FirebaseFirestoreException("Failed to get recipe document", FirebaseFirestoreException.Code.UNKNOWN))
            }

            val recipe = snapshot.toObject(RecipeSerializable::class.java)!!

            return Result.Success(recipe.toRecipe())
    }

    class RecipeDataSourceException(message: String? = null, cause: Throwable? = null, val code: ExceptionCode) : Exception(message, cause) {
        constructor(cause: Throwable, code: ExceptionCode) : this(null, cause, code)
        enum class ExceptionCode {
            RECIPE_UPLOAD_FAILED,
            RECIPE_PREVIEW_UPLOAD_FAILED,
            IMAGE_URL_DOWNLOAD_FAILED,
            IMAGE_UPLOAD_FAILED
        }
    }

    private companion object {
        const val RECIPE_IMAGES_FOLDER = "recipe_images"
    }
}