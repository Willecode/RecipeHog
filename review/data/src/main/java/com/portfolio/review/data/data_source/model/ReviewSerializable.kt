package com.portfolio.review.data.data_source.model

import com.portfolio.review.domain.Review

data class ReviewSerializable(
    val author: String = "",
    val authorUserId: String = "",
    val stars: Int = 0,
    val body: String = ""
)

fun ReviewSerializable.toReview(): Review {
    return Review(
        author = author, authorUserId = authorUserId, stars = stars, body = body
    )
}

fun Review.toReviewSerializable(): ReviewSerializable {
    return ReviewSerializable(
        author = author, authorUserId = authorUserId, stars = stars, body = body
    )
}