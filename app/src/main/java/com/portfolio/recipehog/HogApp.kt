package com.portfolio.recipehog

import android.app.Application
import com.portfolio.auth.data.di.authDataModule
import com.portfolio.auth.presentation.di.authViewModelModule
import com.portfolio.bookmarks.presentation.di.bookmarksPresentationModule
import com.portfolio.core.data.di.coreDataModule
import com.portfolio.data.di.discoverDataModule
import com.portfolio.home.data.di.homeDataModule
import com.portfolio.home.presentation.di.homeViewModelModule
import com.portfolio.presentation.di.discoverViewModelModule
import com.portfolio.profile.presentation.di.profilePresentationModule
import com.portfolio.recipe.data.di.recipeDataModule
import com.portfolio.recipe.presentation.di.recipePresentationModule
import com.portfolio.recipehog.di.mainModule
import com.portfolio.review.data.di.reviewDataModule
import com.portfolio.review.presentation.di.reviewPresentationModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin

class HogApp: Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@HogApp)
            workManagerFactory()
            modules(
                authViewModelModule,
                authDataModule,
                mainModule,
                homeDataModule,
                homeViewModelModule,
                recipeDataModule,
                recipePresentationModule,
                coreDataModule,
                discoverDataModule,
                discoverViewModelModule,
                bookmarksPresentationModule,
                profilePresentationModule,
                reviewDataModule,
                reviewPresentationModule
            )
        }
    }
}