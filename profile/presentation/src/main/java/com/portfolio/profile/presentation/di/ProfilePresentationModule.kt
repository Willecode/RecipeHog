package com.portfolio.profile.presentation.di

import com.portfolio.profile.presentation.edit_profile.EditProfileViewModel
import com.portfolio.profile.presentation.view_profile.ViewProfileViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val profilePresentationModule = module {
    viewModelOf(::ViewProfileViewModel)
    viewModelOf(::EditProfileViewModel)
}