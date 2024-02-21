package com.juanarton.batterysense.di

import com.juanarton.core.data.domain.batteryInfo.usecase.AppConfigInteractor
import com.juanarton.core.data.domain.batteryInfo.usecase.AppConfigUseCase
import com.juanarton.core.data.domain.batteryMonitoring.usecase.BatteryMonitoringInteractor
import com.juanarton.core.data.domain.batteryMonitoring.usecase.BatteryMonitoringUseCase
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
    abstract fun provideBatteryInfoRepositoryUseCase(
        appConfigInteractor: AppConfigInteractor
    ): AppConfigUseCase

    @Binds
    @ViewModelScoped
    abstract fun provideBatteryMonitoringRepositoryUseCase(
        batteryMonitoringInteractor: BatteryMonitoringInteractor
    ): BatteryMonitoringUseCase
}