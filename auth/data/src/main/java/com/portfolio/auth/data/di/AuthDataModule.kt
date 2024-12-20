package com.portfolio.auth.data.di

import com.google.firebase.auth.FirebaseAuth
import com.portfolio.auth.data.AndroidEmailValidator
import com.portfolio.auth.data.BuildConfig
import com.portfolio.auth.data.FirebaseAuthRepository
import com.portfolio.auth.data.FirebaseSessionStorage
import com.portfolio.auth.domain.AuthRepository
import com.portfolio.auth.domain.EmailValidator
import com.portfolio.auth.domain.RegisterCredentialValidator
import com.portfolio.core.domain.model.SessionStorage
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authDataModule = module {
    single {
        val auth = FirebaseAuth.getInstance()
        if(BuildConfig.DEBUG)
            auth.useEmulator(BuildConfig.EMU_HOST, BuildConfig.EMU_AUTH_PORT)
        auth
    }
    singleOf(::FirebaseAuthRepository).bind<AuthRepository>()
    singleOf(::AndroidEmailValidator).bind<EmailValidator>()
    singleOf(::RegisterCredentialValidator)
    singleOf(::FirebaseSessionStorage).bind<SessionStorage>()

}