package com.portfolio.recipehog.di

import com.portfolio.core.domain.use_case.GetCurrentUserLikeAndBookmarkStateUseCase
import com.portfolio.recipehog.HogApp
import com.portfolio.recipehog.MainViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val mainModule= module {
    viewModelOf(::MainViewModel)
    singleOf(::GetCurrentUserLikeAndBookmarkStateUseCase)
    single<CoroutineScope> {
        (androidApplication() as HogApp).applicationScope
    }
}