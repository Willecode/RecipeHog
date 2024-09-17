package com.portfolio.auth.data.di

import com.google.firebase.auth.FirebaseAuth
import com.portfolio.auth.data.AndroidEmailValidator
import com.portfolio.auth.data.FirebaseAuthRepository
import com.portfolio.auth.data.FirebaseSessionStorage
import com.portfolio.auth.domain.AuthRepository
import com.portfolio.auth.domain.EmailValidator
import com.portfolio.auth.domain.RegisterCredentialValidator
import com.portfolio.core.domain.SessionStorage
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authDataModule = module {
    single {
        FirebaseAuth.getInstance()
    }
    singleOf(::FirebaseAuthRepository).bind<AuthRepository>()
    singleOf(::AndroidEmailValidator).bind<EmailValidator>()
    singleOf(::RegisterCredentialValidator)
    singleOf(::FirebaseSessionStorage).bind<SessionStorage>()

}