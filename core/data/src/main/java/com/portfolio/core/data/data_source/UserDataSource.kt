package com.portfolio.core.data.data_source

import com.portfolio.core.domain.model.UserData
import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.EmptyResult
import com.portfolio.core.domain.util.Result

/**
 * User data source that fulfills the purpose of both local and remote data source.
 * Intended for solutions that do not make total separation of remote and local functionality
 * possible - such as Firestore.
 */
interface UserDataSource {
    suspend fun getUserData(userId: String, includePrivateData: Boolean): Result<UserData, DataError.Network>
    suspend fun getUserDataFromCache(userId: String, includePrivateData: Boolean): Result<UserData, DataError.Network>
    suspend fun getUserDataFromServer(userId: String, includePrivateData: Boolean): Result<UserData, DataError.Network>
    suspend fun likeRecipe(userId: String, recipeId: String): EmptyResult<DataError.Network>
    suspend fun unlikeRecipe(userId: String, recipeId: String): EmptyResult<DataError.Network>
    suspend fun bookmarkRecipe(userId: String, recipeId: String): EmptyResult<DataError.Network>
    suspend fun unbookmarkRecipe(userId: String, recipeId: String): EmptyResult<DataError.Network>
}