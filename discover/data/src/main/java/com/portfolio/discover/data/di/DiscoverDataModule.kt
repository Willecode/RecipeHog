package com.portfolio.discover.data.di

import com.portfolio.discover.data.DefaultDiscoverRepository
import com.portfolio.discover.data.data_source.DiscoverDataSource
import com.portfolio.discover.data.data_source.FirebaseDiscoverDatasource
import com.portfolio.domain.DiscoverRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val discoverDataModule = module {
    factoryOf(::FirebaseDiscoverDatasource).bind<DiscoverDataSource>()
    factoryOf(::DefaultDiscoverRepository).bind<DiscoverRepository>()
}