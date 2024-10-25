package com.portfolio.review.domain

import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.EmptyResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {
    /**
     * @return flow that observes loaded/fetched reviews
     * @param coroutineScope coroutine scope that the repository uses to perform logic that is only relevant
     * in client's scope.
     */
    fun getReviews(recipeId: String, coroutineScope: CoroutineScope): Flow<List<Review>>
    suspend fun fetchReviews(recipeId: String): EmptyResult<DataError.Network>
    suspend fun postReview(review: Review, recipeId: String): EmptyResult<DataError.Network>
}