package com.portfolio.core.data.repository

import com.portfolio.core.data.data_source.UserDataSource
import com.portfolio.core.domain.model.SessionStorage
import com.portfolio.core.domain.model.UserData
import com.portfolio.core.domain.model.UserDataRepository
import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.EmptyResult
import com.portfolio.core.domain.util.Result

class OfflineFirstFirebaseUserDataRepository(
    private val userDataSource: UserDataSource,
    private val sessionStorage: SessionStorage
): UserDataRepository {
    override suspend fun getUserData(userId: String): Result<UserData, DataError.Network> {
        return userDataSource.getUserData(userId = userId, includePrivateData = false)
    }

    override suspend fun getUserDataFromCache(userId: String): Result<UserData, DataError.Network> {
        return userDataSource.getUserDataFromCache(userId = userId, includePrivateData = false)
    }

    override suspend fun getUserDataFromServer(userId: String): Result<UserData, DataError.Network> {
        return userDataSource.getUserDataFromServer(userId = userId, includePrivateData = false)
    }

    override suspend fun getCurrentUserData(): Result<UserData, DataError.Network> {
        val uid = sessionStorage.get()?.userId ?: return Result.Error(DataError.Network.UNAUTHORIZED)
        return userDataSource.getUserData(userId = uid, includePrivateData = true)
    }

    override suspend fun getCurrentUserDataFromCache(): Result<UserData, DataError.Network> {
        val uid = sessionStorage.get()?.userId ?: return Result.Error(DataError.Network.UNAUTHORIZED)
        return userDataSource.getUserDataFromCache(userId = uid, includePrivateData = true)

    }

    override suspend fun getCurrentUserDataFromServer(): Result<UserData, DataError.Network> {
        val uid = sessionStorage.get()?.userId ?: return Result.Error(DataError.Network.UNAUTHORIZED)
        return userDataSource.getUserDataFromServer(userId = uid, includePrivateData = true)

    }

    override suspend fun likeRecipe(recipeId: String): EmptyResult<DataError.Network> {
        val uid = sessionStorage.get()?.userId ?: return Result.Error(DataError.Network.UNAUTHORIZED)
        return userDataSource.likeRecipe(userId = uid, recipeId = recipeId)
    }

    override suspend fun unlikeRecipe(recipeId: String): EmptyResult<DataError.Network> {
        val uid = sessionStorage.get()?.userId ?: return Result.Error(DataError.Network.UNAUTHORIZED)
        return userDataSource.unlikeRecipe(userId = uid, recipeId = recipeId)
    }

    override suspend fun bookmarkRecipe(recipeId: String): EmptyResult<DataError.Network> {
        val uid = sessionStorage.get()?.userId ?: return Result.Error(DataError.Network.UNAUTHORIZED)
        return userDataSource.bookmarkRecipe(userId = uid, recipeId = recipeId)
    }

    override suspend fun unbookmarkRecipe(recipeId: String): EmptyResult<DataError.Network> {
        val uid = sessionStorage.get()?.userId ?: return Result.Error(DataError.Network.UNAUTHORIZED)
        return userDataSource.unbookmarkRecipe(userId = uid, recipeId = recipeId)
    }

}