package com.portfolio.recipehog

import android.app.Application
import com.portfolio.auth.data.di.authDataModule
import com.portfolio.auth.presentation.di.authViewModelModule
import com.portfolio.recipehog.di.mainViewModelModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class HogApp: Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@HogApp)
            //workManagerFactory()
            modules(
                authViewModelModule,
                authDataModule,
                mainViewModelModule
            )
        }
    }
}