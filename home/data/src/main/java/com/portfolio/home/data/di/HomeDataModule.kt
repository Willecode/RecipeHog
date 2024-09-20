package com.portfolio.home.data.di

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.portfolio.home.data.FirebaseRecipeRepository
import com.portfolio.home.domain.RecipeRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val homeDataModule = module {
    single {
        val firestore = Firebase.firestore
        firestore.useEmulator("10.0.2.2", 8080)

        firestore.firestoreSettings = firestoreSettings {
            isPersistenceEnabled = false
        }

        firestore
    }
    singleOf(::FirebaseRecipeRepository).bind<RecipeRepository>()
}