package com.portfolio.core.data.data_source

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenSource
import com.google.firebase.firestore.SnapshotListenOptions
import com.google.firebase.firestore.Source
import com.portfolio.core.data.FirebaseConstants.USER_BOOKMARKED_RECIPES_DOCUMENT
import com.portfolio.core.data.FirebaseConstants.USER_COLLECTION
import com.portfolio.core.data.FirebaseConstants.USER_LIKED_RECIPES_DOCUMENT
import com.portfolio.core.data.FirebaseConstants.USER_PRIVATE_DATA_COLLECTION
import com.portfolio.core.data.FirebaseConstants.USER_PROFILE_PIC_URL_FIELD
import com.portfolio.core.data.data_source.model.PublicUserDataSerializable
import com.portfolio.core.data.data_source.model.RecipePreviewListSerializable
import com.portfolio.core.data.data_source.model.toPublicUserData
import com.portfolio.core.data.data_source.util.toPrivateUserData
import com.portfolio.core.data.util.FirebaseStorageUploader
import com.portfolio.core.data.util.firestoreSafeCallServer
import com.portfolio.core.data.util.toRecipePreview
import com.portfolio.core.domain.model.RecipePreview
import com.portfolio.core.domain.model.UserData
import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.EmptyResult
import com.portfolio.core.domain.util.Result
import com.portfolio.core.domain.util.asEmptyDataResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseReactiveUserDataSource(
    private val userDataSource: UserDataSource,
    private val firestore: FirebaseFirestore,
    private val firebaseStorageUploader: FirebaseStorageUploader
): ReactiveUserDataSource {

    /**
     * Adds a listener to the cache for user data changes and emits it to a flow
     * TODO: Remove UserDataSource class, implement the methods here.
     */
    override fun getUserData(userId: String): Flow<UserData> {
        val publicDataRef = firestore
            .collection(USER_COLLECTION)
            .document(userId)

        val privateDataRef = firestore
            .collection(USER_COLLECTION)
            .document(userId)
            .collection(USER_PRIVATE_DATA_COLLECTION)

        val options = SnapshotListenOptions.Builder()
            .setSource(ListenSource.CACHE)
            .build();

        val publicFlow = callbackFlow {
            val listener = publicDataRef.addSnapshotListener(options) { snapshotPublicData, error ->
                snapshotPublicData?.toObject(PublicUserDataSerializable::class.java)?.let{
                    trySend(it.toPublicUserData())
                }
            }
            awaitClose {
                listener.remove()
            }
        }

        val privateFlow = callbackFlow {
            val listener = privateDataRef.addSnapshotListener(options) { snapshotPrivateData, error ->
                snapshotPrivateData?.let{
                    trySend(it.toPrivateUserData())
                }
            }
            awaitClose {
                listener.remove()
            }
        }

        val nullFlow = flow {
            emit(null)
        }
        return combine(publicFlow, merge(privateFlow, nullFlow)) { public, private ->
            UserData(publicUserData = public, privateUserData = private)
        }
    }

    override suspend fun fetchUserData(
        userId: String,
        includePrivateData: Boolean
    ): EmptyResult<DataError.Network> {
        return userDataSource.getUserDataFromServer(
            userId = userId,
            includePrivateData = includePrivateData
        ).asEmptyDataResult()
    }

    override suspend fun likeRecipe(
        userId: String,
        recipeId: String
    ): EmptyResult<DataError.Network> {
        return userDataSource.likeRecipe(userId = userId, recipeId = recipeId)
    }

    override suspend fun unlikeRecipe(
        userId: String,
        recipeId: String
    ): EmptyResult<DataError.Network> {
        return userDataSource.unlikeRecipe(userId = userId, recipeId = recipeId)
    }

    override suspend fun bookmarkRecipe(
        userId: String,
        recipeId: String
    ): EmptyResult<DataError.Network> {
        return userDataSource.bookmarkRecipe(userId = userId, recipeId = recipeId)
    }

    override suspend fun unbookmarkRecipe(
        userId: String,
        recipeId: String
    ): EmptyResult<DataError.Network> {
        return userDataSource.unbookmarkRecipe(userId = userId, recipeId = recipeId)
    }

    override fun getBookmarkedRecipes(userId: String): Flow<List<RecipePreview>> {
        val docRef = firestore
            .collection(USER_COLLECTION)
            .document(userId)
            .collection(USER_PRIVATE_DATA_COLLECTION)
            .document(USER_BOOKMARKED_RECIPES_DOCUMENT)

        val initialFlow = flow {
            emit(null)
            try {
                val doc = docRef.get(Source.CACHE).await()
                doc?.toObject(RecipePreviewListSerializable::class.java)?.let {serializableList ->
                    val recipePreviewList = serializableList.content.toList().map {
                        it.second.toRecipePreview()
                    }
                    emit(recipePreviewList)
                }
            } catch (_: FirebaseFirestoreException) {}
        }

        val options = SnapshotListenOptions.Builder()
            .setSource(ListenSource.CACHE)
            .build();

        val updateFlow = callbackFlow {
            trySend(null)
            val listener = docRef.addSnapshotListener(options) { snapshotPrivateData, error ->
                snapshotPrivateData?.toObject(RecipePreviewListSerializable::class.java)?.let { serializableList ->
                    val recipePreviewList = serializableList.content.toList().map {
                        it.second.toRecipePreview()
                    }
                    trySend(recipePreviewList)
                }
            }
            awaitClose {
                listener.remove()
            }
        }

        return combine(initialFlow, updateFlow) { initial, update ->
            update ?: initial
        }.mapNotNull { it }
    }

    override fun getLikedRecipes(userId: String): Flow<List<RecipePreview>> {
        val docRef = firestore
            .collection(USER_COLLECTION)
            .document(userId)
            .collection(USER_PRIVATE_DATA_COLLECTION)
            .document(USER_LIKED_RECIPES_DOCUMENT)

        val initialFlow = flow {
            emit(null)
            try {
                val doc = docRef.get(Source.CACHE).await()
                doc?.toObject(RecipePreviewListSerializable::class.java)?.let {serializableList ->
                    val recipePreviewList = serializableList.content.toList().map {
                        it.second.toRecipePreview()
                    }
                    emit(recipePreviewList)
                }
            } catch (_: FirebaseFirestoreException) {}
        }

        val options = SnapshotListenOptions.Builder()
            .setSource(ListenSource.CACHE)
            .build();

        val updateFlow = callbackFlow {
            trySend(null)
            val listener = docRef.addSnapshotListener(options) { snapshotPrivateData, error ->
                snapshotPrivateData?.toObject(RecipePreviewListSerializable::class.java)?.let { serializableList ->
                    val recipePreviewList = serializableList.content.toList().map {
                        it.second.toRecipePreview()
                    }
                    trySend(recipePreviewList)
                }
            }
            awaitClose {
                listener.remove()
            }
        }

        return combine(initialFlow, updateFlow) { initial, update ->
            update ?: initial
        }.mapNotNull { it }
    }

    override suspend fun fetchBookmarksAndLikes(userId: String): EmptyResult<DataError.Network> {
        return firestoreSafeCallServer {
            val collectionRef = firestore
                .collection(USER_COLLECTION)
                .document(userId)
                .collection(USER_PRIVATE_DATA_COLLECTION)

            collectionRef.get().await()

            return Result.Success(Unit).asEmptyDataResult()
        }
    }

    /**
     * Todo: Delete old profile image from storage
     */
    override suspend fun changeProfilePicture(
        userId: String,
        filePath: String
    ): EmptyResult<DataError.Network> {
        return firestoreSafeCallServer {
            val storageUploadPath = generateStorageUploadPath()
            val imgUrl = firebaseStorageUploader.tryUploadImage(filePath, storageUploadPath)
            try {
                firestore
                    .collection(USER_COLLECTION)
                    .document(userId)
                    .update(
                        /* field = */ USER_PROFILE_PIC_URL_FIELD,
                        /* value = */ imgUrl
                    ).await()
            } catch (e: FirebaseFirestoreException) {
                firebaseStorageUploader.scheduleUploadedFileDeletion(storageUploadPath)
                throw e
            }

            return Result.Success(Unit).asEmptyDataResult()
        }
    }



    /**
     * Returns the path that the file will have in the remote database
     */
    private fun generateStorageUploadPath() = "$PROFILE_IMAGES_FOLDER/${generateRandomId()}.jpg)"

    private fun generateRandomId() = UUID.randomUUID().toString()

    companion object {
        const val PROFILE_IMAGES_FOLDER = "profile_images"
    }
}