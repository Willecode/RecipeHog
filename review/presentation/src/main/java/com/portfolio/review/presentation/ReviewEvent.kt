package com.portfolio.review.presentation

import com.portfolio.core.presentation.ui.UiText

interface ReviewEvent {
    data class ReviewError(val error: UiText): ReviewEvent
    data object ReviewPostedSuccessfully: ReviewEvent
    data object AuthError: ReviewEvent
}