package com.portfolio.review.data.data_source

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Source
import com.portfolio.core.data.FirebaseConstants.REVIEWS_COLLECTION
import com.portfolio.core.data.FirebaseConstants.REVIEWS_CONTENT_FIELD
import com.portfolio.core.data.util.firestoreSafeCallCache
import com.portfolio.core.data.util.firestoreSafeCallServer
import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.EmptyResult
import com.portfolio.core.domain.util.Result
import com.portfolio.core.domain.util.asEmptyDataResult
import com.portfolio.review.domain.Review
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await

class FirebaseReviewDataSource(
    private val firestore: FirebaseFirestore
): ReviewDataSource {

    private val reviewFlow = MutableStateFlow<List<ReviewSerializable>>(listOf())

    override fun getReviews(recipeId: String): Flow<List<Review>> =
        reviewFlow.map { serializables ->
            serializables.map {
                it.toReview()
            }
        }

    override suspend fun loadReviewsFromCache(recipeId: String): EmptyResult<DataError> {
        return firestoreSafeCallCache {
            return fetchReviews(recipeId, source = Source.CACHE)
        }
    }

    override suspend fun fetchReviewsFromServer(recipeId: String): EmptyResult<DataError.Network> {
        return firestoreSafeCallServer {
            return fetchReviews(recipeId, source = Source.SERVER)
        }
    }

    override suspend fun postReview(review: Review, recipeId: String): EmptyResult<DataError.Network> {
        return firestoreSafeCallServer {
            val docRef = firestore
                .collection(REVIEWS_COLLECTION)
                .document(recipeId)

            try {
                docRef.update(
                    REVIEWS_CONTENT_FIELD,
                    FieldValue.arrayUnion(review)
                ).await()
            } catch (e: FirebaseFirestoreException) {
                when (e.code) {
                    FirebaseFirestoreException.Code.NOT_FOUND -> {
                        // Doc didn't exist, create it instead
                        docRef.set(ReviewDocDtoUpload(content = listOf(review)))
                    }
                    else -> throw e
                }
            }
            return Result.Success(Unit).asEmptyDataResult()
        }
    }

    private suspend fun fetchReviews(recipeId: String, source: Source): EmptyResult<DataError.Network> {
        val docRef = firestore
            .collection(REVIEWS_COLLECTION)
            .document(recipeId)
            .get(source)
            .await()

        val dto = docRef?.toObject(ReviewDocDtoDownload::class.java)
            ?: throw FirebaseFirestoreException("Fetched review doc was null", FirebaseFirestoreException.Code.UNKNOWN)

        reviewFlow.update {
            dto.content
        }

        return Result.Success(Unit).asEmptyDataResult()
    }

    data class ReviewDocDtoDownload(
        val content: List<ReviewSerializable> = listOf()
    )

    data class ReviewDocDtoUpload(
        val content: List<Review> = listOf()
    )

    data class ReviewSerializable(
        val author: String = "",
        val authorUserId: String = "",
        val stars: Int = 0,
        val body: String = ""
    )

    private fun ReviewSerializable.toReview(): Review {
        return Review(
            author = author, authorUserId = authorUserId, stars = stars, body = body
        )
    }
}