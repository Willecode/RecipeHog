package com.portfolio.recipe.data.di

import com.google.firebase.storage.FirebaseStorage
import com.portfolio.core.data.work.DeleteStorageFileScheduler
import com.portfolio.core.data.work.DeleteStorageFileWorker
import com.portfolio.recipe.data.FirestoreRecipeRepository
import com.portfolio.recipe.data.data_source.FirebaseRecipeDataSource
import com.portfolio.recipe.data.data_source.RecipeDataSource
import com.portfolio.recipe.domain.RecipeRepository
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val recipeDataModule = module {
    singleOf(::FirestoreRecipeRepository).bind<RecipeRepository>()
    singleOf(::FirebaseRecipeDataSource).bind<RecipeDataSource>()
    single{
        val storage = FirebaseStorage.getInstance()
        storage.useEmulator("10.0.2.2", 9199)
        storage
    }
    workerOf(::DeleteStorageFileWorker)
    singleOf(::DeleteStorageFileScheduler)
}