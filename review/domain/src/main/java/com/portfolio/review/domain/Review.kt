package com.portfolio.review.domain


data class Review(
    val author: String,
    val authorUserId: String,
    val stars: Int,
    val body: String
)
