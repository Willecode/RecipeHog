package com.portfolio.recipehog.di

import com.portfolio.recipehog.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val mainViewModelModule = module {
    viewModelOf(::MainViewModel)
}