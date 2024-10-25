package com.portfolio.review.presentation

sealed interface ReviewAction {
    data class OnAuthorClick(val authorId: String): ReviewAction
    data class OnPostReview(val body: String, val rating: Int): ReviewAction
}