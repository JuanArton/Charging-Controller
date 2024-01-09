package com.juanarton.core.di

import com.juanarton.core.data.domain.repository.DataRepositoryInterface
import com.juanarton.core.data.repository.DataRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun provideRepository(dataRepository: DataRepository): DataRepositoryInterface
}