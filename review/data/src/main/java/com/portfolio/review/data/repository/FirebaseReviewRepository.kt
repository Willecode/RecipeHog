package com.portfolio.review.data.repository

import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.EmptyResult
import com.portfolio.core.domain.util.Result
import com.portfolio.review.data.data_source.ReviewDataSource
import com.portfolio.review.domain.Review
import com.portfolio.review.domain.ReviewRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FirebaseReviewRepository(
    private val reviewDataSource: ReviewDataSource
): ReviewRepository {
    override fun getReviews(recipeId: String, coroutineScope: CoroutineScope): Flow<List<Review>> {
        coroutineScope.launch {
            withContext(Dispatchers.IO){
                val result = reviewDataSource.loadReviewsFromCache(recipeId)
                when (result) {
                    is Result.Error -> fetchReviews(recipeId)
                    is Result.Success -> Unit
                }
            }
        }

        return reviewDataSource.getReviews(recipeId)
    }

    override suspend fun fetchReviews(recipeId: String): EmptyResult<DataError.Network> {
        return reviewDataSource.fetchReviewsFromServer(recipeId)
    }

    override suspend fun postReview(review: Review, recipeId: String): EmptyResult<DataError.Network> {
        return reviewDataSource.postReview(review = review, recipeId = recipeId)
    }
}