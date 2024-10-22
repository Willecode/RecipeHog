package com.portfolio.recipe.data.data_source

import android.net.Uri
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Source
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.portfolio.core.data.FirebaseConstants.RECIPES_COLLECTION
import com.portfolio.core.data.FirebaseConstants.RECIPE_PREVIEWS_COLLECTION
import com.portfolio.core.data.util.firestoreSafeCallCache
import com.portfolio.core.data.util.firestoreSafeCallServer
import com.portfolio.core.domain.model.IngredientListing
import com.portfolio.core.domain.model.Recipe
import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.EmptyResult
import com.portfolio.core.domain.util.Result
import com.portfolio.core.domain.util.asEmptyDataResult
import com.portfolio.recipe.data.data_source.work.DeleteStorageFileScheduler
import com.portfolio.recipe.domain.RecipeDraft
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import java.io.File
import java.util.UUID

class FirebaseRecipeDataSource(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val deleteStorageFileScheduler: DeleteStorageFileScheduler
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

    override suspend fun postRecipe(
        recipeDraft: RecipeDraft,
        imageFilePath: String,
        username: String,
        userId: String
    ): EmptyResult<DataError.Network> {
        return firestoreSafeCallServer {
            val storageUploadPath = generateStorageUploadPath()
            val imgUrl = tryUploadImage(imageFilePath, storageUploadPath)
            try {
                uploadRecipeAndPreview(recipeDraft, imgUrl, userId, username)
            } catch (e: FirebaseFirestoreException) {
                deleteStorageFileScheduler.scheduleFileDeletion(storageUploadPath)
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
            uploadRecipePreview(
                recipeId = recipeId,
                recipePreviewUploadable = recipeDraft.toRecipePreviewUploadable(
                    imgUrl = imgUrl,
                    authorId = userId,
                    author = username
                )
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
        firestore
            .collection(RECIPE_PREVIEWS_COLLECTION)
            .document(recipeId)
            .set(
                recipePreviewUploadable
            ).await()
    }

    private suspend fun FirebaseRecipeDataSource.tryUploadImage(
        localImageFilePath: String,
        storageUploadPath: String
    ) = try {
        uploadImageFile(localImageFilePath, storageUploadPath)
    } catch (e: StorageException) {
        // throw an exception that the generic safecall can handle
        throw FirebaseFirestoreException(
            "Failed to upload image",
            FirebaseFirestoreException.Code.UNKNOWN
        )
    } catch (e: TimeoutCancellationException) {
        throw FirebaseFirestoreException(
            "Failed to upload image",
            FirebaseFirestoreException.Code.UNKNOWN
        )
    }

    /**
     * Returns the path that the file will have in the remote database
     */
    private fun generateStorageUploadPath() = "$RECIPE_IMAGES_FOLDER/${generateRandomId()}.jpg)"

    private suspend fun uploadImageFile(localImageFilePath: String, storageUploadPath: String): String {
        val file = Uri.fromFile(File(localImageFilePath))
        val imageRef = storage.reference.child(storageUploadPath)

        try {
            withTimeout(20_000L) {
                imageRef.putFile(file).await()
            }
        } catch (e: TimeoutCancellationException) {
            throw e
        }

        try {
            val imageUrlSnapshot = withTimeout(20_000L) {
                imageRef.downloadUrl.await()
            }
            val imgUrl = imageUrlSnapshot.toString()
            return imgUrl
        } catch (e: StorageException) {
            // failed to download URL, need to delete the file from the database since nothing can reference it
            deleteStorageFileScheduler.scheduleFileDeletion(storageUploadPath)
            throw e
        } catch (e: TimeoutCancellationException) {
            deleteStorageFileScheduler.scheduleFileDeletion(storageUploadPath)
            throw e
        }
    }

    private fun generateRandomId() = UUID.randomUUID().toString()

    private suspend fun getRecipe(recipeId: String, source: Source): Result<Recipe, DataError.Network> {
            val snapshot = firestore.collection("recipes")
                .document(recipeId)
                .get(source)
                .await()

            if (snapshot == null) {
                throw(FirebaseFirestoreException("Failed to get recipe document", FirebaseFirestoreException.Code.UNKNOWN))
            }

            val recipe = snapshot.toObject(Recipe::class.java)!!

            return Result.Success(recipe)
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