package com.portfolio.home.data.di

import com.portfolio.home.data.FirebaseHomeRepository
import com.portfolio.home.domain.HomeRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val homeDataModule = module {
    singleOf(::FirebaseHomeRepository).bind<HomeRepository>()
}