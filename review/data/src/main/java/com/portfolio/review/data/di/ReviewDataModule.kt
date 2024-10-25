package com.portfolio.review.data.di

import com.portfolio.review.data.data_source.FirebaseReviewDataSource
import com.portfolio.review.data.data_source.ReviewDataSource
import com.portfolio.review.data.repository.FirebaseReviewRepository
import com.portfolio.review.domain.ReviewRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val reviewDataModule = module {
    singleOf(::FirebaseReviewDataSource).bind<ReviewDataSource>()
    singleOf(::FirebaseReviewRepository).bind<ReviewRepository>()
}