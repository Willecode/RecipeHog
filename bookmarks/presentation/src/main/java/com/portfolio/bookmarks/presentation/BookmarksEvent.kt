package com.portfolio.bookmarks.presentation

import com.portfolio.core.presentation.ui.UiText

sealed interface BookmarksEvent {
    data class BookmarksError(val error: UiText): BookmarksEvent
    data object AuthError: BookmarksEvent
}