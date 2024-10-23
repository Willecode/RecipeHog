package com.portfolio.core.data.data_source

import com.portfolio.core.domain.model.RecipePreview
import com.portfolio.core.domain.model.UserData
import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow

/**
 * Reactive user data source that fulfills the purpose of both local and remote data source.
 * Intended for solutions that do not make total separation of remote and local functionality
 * possible - such as Firestore.
 */
interface ReactiveUserDataSource {
    fun getUserData(userId: String): Flow<UserData>
    suspend fun fetchUserData(userId: String, includePrivateData: Boolean): EmptyResult<DataError.Network>

    suspend fun likeRecipe(userId: String, recipeId: String): EmptyResult<DataError.Network>
    suspend fun unlikeRecipe(userId: String, recipeId: String): EmptyResult<DataError.Network>
    suspend fun bookmarkRecipe(userId: String, recipeId: String): EmptyResult<DataError.Network>
    suspend fun unbookmarkRecipe(userId: String, recipeId: String): EmptyResult<DataError.Network>

    fun getBookmarkedRecipes(userId: String): Flow<List<RecipePreview>>
    fun getLikedRecipes(userId: String): Flow<List<RecipePreview>>
    suspend fun fetchBookmarksAndLikes(userId: String): EmptyResult<DataError.Network>
}