package com.juanarton.chargingcurrentcontroller.di

import com.juanarton.core.data.domain.usecase.DataRepositoryInteractor
import com.juanarton.core.data.domain.usecase.DataRepositoryUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class AppModule {

    @Binds
    @ViewModelScoped
    abstract fun provideDataRepositoryUseCase(repositoryInteractor: DataRepositoryInteractor): DataRepositoryUseCase
}