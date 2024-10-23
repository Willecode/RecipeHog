package com.portfolio.core.domain.model

import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow

interface ReactiveUserDataRepository {
    fun getCurrentUserData(): Flow<UserData>
    suspend fun getUserData(userId: String): Flow<UserData>

    suspend fun fetchCurrentUserData(): EmptyResult<DataError.Network>
    suspend fun fetchUserData(userId: String): EmptyResult<DataError.Network>

    suspend fun likeRecipe(recipeId: String): EmptyResult<DataError.Network>
    suspend fun unlikeRecipe(recipeId: String): EmptyResult<DataError.Network>
    suspend fun bookmarkRecipe(recipeId: String): EmptyResult<DataError.Network>
    suspend fun unbookmarkRecipe(recipeId: String): EmptyResult<DataError.Network>

    fun getBookmarkedRecipes(): Flow<List<RecipePreview>>
    fun getLikedRecipes(): Flow<List<RecipePreview>>
    suspend fun fetchBookmarksAndLikes(): EmptyResult<DataError.Network>
}