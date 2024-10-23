package com.portfolio.bookmarks.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portfolio.core.domain.model.ReactiveUserDataRepository
import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.Result
import com.portfolio.core.presentation.ui.asUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class BookmarksViewModel(
    private val userDataRepository: ReactiveUserDataRepository
): ViewModel() {

    var state by mutableStateOf(BookmarksState())
        private set

    private val _eventChannel = Channel<BookmarksEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            userDataRepository.getBookmarkedRecipes().collect{
                state = state.copy(bookmarks = it)
            }
        }
        viewModelScope.launch {
            userDataRepository.getLikedRecipes().collect{
                state = state.copy(liked = it)
            }
        }
        viewModelScope.launch {
            val result = userDataRepository.fetchBookmarksAndLikes()
            when (result) {
                is Result.Error -> {
                    handleError(result.error)
                }
                is Result.Success -> Unit
            }
        }
    }

    fun onAction(action: BookmarksAction) {

    }

    private suspend fun handleError(error: DataError) {
        if (error == DataError.Network.UNAUTHORIZED)
            _eventChannel.send(BookmarksEvent.AuthError)
        when (error) {
            DataError.Local.UNAVAILABLE -> Unit
            DataError.Network.UNAVAILABLE -> Unit
            else -> _eventChannel.send(BookmarksEvent.BookmarksError(error.asUiText()))
        }
    }
}
