package com.portfolio.core.data.repository

import com.portfolio.core.data.data_source.ReactiveUserDataSource
import com.portfolio.core.domain.model.ReactiveUserDataRepository
import com.portfolio.core.domain.model.SessionStorage
import com.portfolio.core.domain.model.UserData
import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.EmptyResult
import com.portfolio.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class OfflineFirstReactiveUserDataRepository(
    private val reactiveUserDataSource: ReactiveUserDataSource,
    private val sessionStorage: SessionStorage
) :ReactiveUserDataRepository
{
    override fun getCurrentUserData(): Flow<UserData> {
        val uid = sessionStorage.get()?.userId ?: return emptyFlow()
        return reactiveUserDataSource.getUserData(uid)
    }

    override suspend fun getUserData(userId: String): Flow<UserData> {
        return reactiveUserDataSource.getUserData(userId)
    }

    override suspend fun fetchUserData(userId: String): EmptyResult<DataError.Network> {
        return reactiveUserDataSource.fetchUserData(userId = userId, includePrivateData = false)
    }

    override suspend fun fetchCurrentUserData(): EmptyResult<DataError.Network> {
        val uid = sessionStorage.get()?.userId ?: return Result.Error(DataError.Network.UNAUTHORIZED)
        return reactiveUserDataSource.fetchUserData(userId = uid, includePrivateData = true)
    }

    override suspend fun likeRecipe(recipeId: String): EmptyResult<DataError.Network> {
        val uid = sessionStorage.get()?.userId ?: return Result.Error(DataError.Network.UNAUTHORIZED)
        return reactiveUserDataSource.likeRecipe(userId = uid, recipeId = recipeId)
    }

    override suspend fun unlikeRecipe(recipeId: String): EmptyResult<DataError.Network> {
        val uid = sessionStorage.get()?.userId ?: return Result.Error(DataError.Network.UNAUTHORIZED)
        return reactiveUserDataSource.unlikeRecipe(userId = uid, recipeId = recipeId)
    }

    override suspend fun bookmarkRecipe(recipeId: String): EmptyResult<DataError.Network> {
        val uid = sessionStorage.get()?.userId ?: return Result.Error(DataError.Network.UNAUTHORIZED)
        return reactiveUserDataSource.bookmarkRecipe(userId = uid, recipeId = recipeId)
    }

    override suspend fun unbookmarkRecipe(recipeId: String): EmptyResult<DataError.Network> {
        val uid = sessionStorage.get()?.userId ?: return Result.Error(DataError.Network.UNAUTHORIZED)
        return reactiveUserDataSource.unbookmarkRecipe(userId = uid, recipeId = recipeId)
    }
}