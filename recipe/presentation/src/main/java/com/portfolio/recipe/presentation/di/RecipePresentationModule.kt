package com.portfolio.recipe.presentation.di

import com.portfolio.recipe.presentation.create_recipe.CreateRecipeViewModel
import com.portfolio.recipe.presentation.view_recipe.ViewRecipeViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val recipePresentationModule = module {
    viewModelOf(::ViewRecipeViewModel)
    viewModelOf(::CreateRecipeViewModel)
}