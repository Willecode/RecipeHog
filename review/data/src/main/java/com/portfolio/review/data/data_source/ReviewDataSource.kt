package com.portfolio.review.data.data_source

import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.EmptyResult
import com.portfolio.review.domain.Review
import kotlinx.coroutines.flow.Flow

interface ReviewDataSource {
    fun getReviews(recipeId: String): Flow<List<Review>>

    /**
     * @return true if cache had reviews, false if not
     */
    suspend fun loadReviewsFromCache(recipeId: String): EmptyResult<DataError>
    suspend fun fetchReviewsFromServer(recipeId: String): EmptyResult<DataError.Network>
    suspend fun postReview(review: Review, recipeId: String): EmptyResult<DataError.Network>
}