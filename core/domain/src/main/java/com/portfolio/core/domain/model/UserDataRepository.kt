package com.portfolio.core.domain.model

import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.EmptyResult
import com.portfolio.core.domain.util.Result

interface UserDataRepository {
    suspend fun getUserData(userId: String): Result<UserData, DataError.Network>
    suspend fun getUserDataFromCache(userId: String): Result<UserData, DataError>
    suspend fun getUserDataFromServer(userId: String): Result<UserData, DataError.Network>
    suspend fun getCurrentUserData(): Result<UserData, DataError.Network>
    suspend fun getCurrentUserDataFromCache(): Result<UserData, DataError>
    suspend fun getCurrentUserDataFromServer(): Result<UserData, DataError.Network>
    suspend fun likeRecipe(recipeId: String): EmptyResult<DataError.Network>
    suspend fun unlikeRecipe(recipeId: String): EmptyResult<DataError.Network>
    suspend fun bookmarkRecipe(recipeId: String): EmptyResult<DataError.Network>
    suspend fun unbookmarkRecipe(recipeId: String): EmptyResult<DataError.Network>
}