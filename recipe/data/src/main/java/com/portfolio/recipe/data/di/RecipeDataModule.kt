package com.portfolio.recipe.data.di

import com.google.firebase.storage.FirebaseStorage
import com.portfolio.recipe.data.FirestoreRecipeRepository
import com.portfolio.recipe.domain.RecipeRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val recipeDataModule = module {
    singleOf(::FirestoreRecipeRepository).bind<RecipeRepository>()
    single{
        val storage = FirebaseStorage.getInstance()
        storage.useEmulator("10.0.2.2", 9199)
        storage
    }
}