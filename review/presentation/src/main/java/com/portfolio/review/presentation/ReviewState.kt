package com.portfolio.review.presentation

data class ReviewState(
    val postingReview: Boolean = false,
    val loadingReviews: Boolean = false,
    val fetchingReviews: Boolean = false
)
