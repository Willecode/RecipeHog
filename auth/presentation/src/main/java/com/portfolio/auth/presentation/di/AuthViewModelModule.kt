package com.portfolio.auth.presentation.di

import com.portfolio.auth.presentation.login.LoginViewModel
import com.portfolio.auth.presentation.register.RegisterViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val authViewModelModule = module {
    viewModelOf(::RegisterViewModel)
    viewModelOf(::LoginViewModel)
}