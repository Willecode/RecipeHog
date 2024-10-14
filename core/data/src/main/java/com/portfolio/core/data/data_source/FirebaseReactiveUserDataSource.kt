package com.portfolio.core.data.data_source

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenSource
import com.google.firebase.firestore.SnapshotListenOptions
import com.portfolio.core.data.FirebaseConstants.USER_COLLECTION
import com.portfolio.core.data.FirebaseConstants.USER_PRIVATE_DATA_COLLECTION
import com.portfolio.core.data.data_source.util.PublicUserDataSerializable
import com.portfolio.core.data.data_source.util.toPrivateUserData
import com.portfolio.core.data.data_source.util.toPublicUserData
import com.portfolio.core.domain.model.UserData
import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.EmptyResult
import com.portfolio.core.domain.util.asEmptyDataResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge

class FirebaseReactiveUserDataSource(
    private val userDataSource: UserDataSource,
    private val firestore: FirebaseFirestore
): ReactiveUserDataSource {

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
                val publicData = snapshotPublicData?.toObject(PublicUserDataSerializable::class.java)?.toPublicUserData()
                    ?: throw FirebaseFirestoreException("Failed to get public user data", FirebaseFirestoreException.Code.UNKNOWN)

                trySend(publicData)
            }
            awaitClose {
                listener.remove()
            }
        }

        val privateFlow = callbackFlow {
            val listener = privateDataRef.addSnapshotListener(options) { snapshotPrivateData, error ->
                val privateData = snapshotPrivateData?.let{
                    it.toPrivateUserData()
                }
                trySend(privateData)
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

}