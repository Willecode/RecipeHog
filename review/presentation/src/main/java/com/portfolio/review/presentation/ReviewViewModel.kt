package com.portfolio.review.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portfolio.core.domain.model.SessionStorage
import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.Result
import com.portfolio.core.presentation.ui.asUiText
import com.portfolio.review.domain.Review
import com.portfolio.review.domain.ReviewRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ReviewViewModel (
    private val reviewRepository: ReviewRepository,
    private val savedStateHandle: SavedStateHandle,
    private val sessionStorage: SessionStorage
): ViewModel() {

    var state by mutableStateOf(ReviewState())
        private set

    private val recipeId = savedStateHandle.get<String>(key = "reviewRecipeId")!!

    val reviews = reviewRepository.getReviews(
        recipeId = recipeId,
        coroutineScope = viewModelScope
    )

    private val _eventChannel = Channel<ReviewEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    fun onAction(action: ReviewAction) {
        when (action) {
            is ReviewAction.OnAuthorClick -> Unit
            is ReviewAction.OnPostReview -> postReview(action.rating, action.body)
        }
    }

    private fun postReview(rating: Int, body: String) {
        viewModelScope.launch {
            state = state.copy(postingReview = true)
            val result = reviewRepository.postReview(
                Review(
                    author = sessionStorage.get()?.userName!!,
                    authorUserId = sessionStorage.get()?.userId!!,
                    stars = rating.coerceIn(minimumValue = 1, maximumValue = 5),
                    body = body.take(MAX_REVIEW_BODY_LENGTH)
                ),
                recipeId = recipeId
            )
            when (result) {
                is Result.Error -> handleError(result.error)
                is Result.Success -> _eventChannel.send(ReviewEvent.ReviewPostedSuccessfully)
            }
            state = state.copy(postingReview = false)
        }
    }

    private suspend fun handleError(error: DataError) {
        if (error == DataError.Network.UNAUTHORIZED)
            _eventChannel.send(ReviewEvent.AuthError)
        when (error) {
            DataError.Local.UNAVAILABLE -> Unit
            DataError.Network.UNAVAILABLE -> Unit
            else -> _eventChannel.send(ReviewEvent.ReviewError(error.asUiText()))
        }
    }

    companion object {
        const val MAX_REVIEW_BODY_LENGTH = 100
    }
}