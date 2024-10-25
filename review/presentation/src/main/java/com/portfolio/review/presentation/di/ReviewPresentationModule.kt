package com.portfolio.review.presentation.di

import com.portfolio.review.presentation.ReviewViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val reviewPresentationModule = module {
    viewModelOf(::ReviewViewModel)
}