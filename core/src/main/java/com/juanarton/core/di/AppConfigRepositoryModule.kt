package com.juanarton.core.di

import com.juanarton.core.data.domain.batteryInfo.repository.IAppConfigRepository
import com.juanarton.core.data.repository.AppConfigRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppConfigRepositoryModule {

    @Binds
    abstract fun provideAppConfigRepository(
        appConfigRepository: AppConfigRepository
    ): IAppConfigRepository
}