package com.portfolio.core.data.di

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.portfolio.core.data.BuildConfig
import com.portfolio.core.data.data_source.FirebaseReactiveUserDataSource
import com.portfolio.core.data.data_source.FirebaseUserDataSource
import com.portfolio.core.data.data_source.ReactiveUserDataSource
import com.portfolio.core.data.data_source.UserDataSource
import com.portfolio.core.data.repository.OfflineFirstFirebaseUserDataRepository
import com.portfolio.core.data.repository.OfflineFirstReactiveUserDataRepository
import com.portfolio.core.data.util.FirebaseStorageUploader
import com.portfolio.core.domain.model.ReactiveUserDataRepository
import com.portfolio.core.domain.model.UserDataRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDataModule = module {
    single {
        val firestore = Firebase.firestore
        if (BuildConfig.DEBUG)
            firestore.useEmulator(BuildConfig.EMU_HOST, BuildConfig.EMU_FIRESTORE_PORT)
        firestore
    }
    singleOf(::FirebaseUserDataSource).bind<UserDataSource>()
    singleOf(::OfflineFirstFirebaseUserDataRepository).bind<UserDataRepository>()
    singleOf(::FirebaseReactiveUserDataSource).bind<ReactiveUserDataSource>()
    singleOf(::OfflineFirstReactiveUserDataRepository).bind<ReactiveUserDataRepository>()
    factoryOf(::FirebaseStorageUploader)
}