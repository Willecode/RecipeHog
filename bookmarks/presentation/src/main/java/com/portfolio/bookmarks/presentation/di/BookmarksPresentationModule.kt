package com.portfolio.bookmarks.presentation.di

import com.portfolio.bookmarks.presentation.BookmarksViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val bookmarksPresentationModule = module {
    viewModelOf(::BookmarksViewModel)
}