package com.juanarton.core.di

import com.juanarton.core.data.source.local.appConfig.LAppConfigDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppConfigDataSourceModule {
    @Singleton
    @Provides
    fun provideLAppConfigDataSource() = LAppConfigDataSource()
}