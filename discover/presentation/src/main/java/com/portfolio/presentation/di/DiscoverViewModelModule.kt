package com.portfolio.presentation.di

import com.portfolio.presentation.DiscoverViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val discoverViewModelModule = module {
    viewModelOf(::DiscoverViewModel)
}