package com.juanarton.chargingcurrentcontroller.di

import com.juanarton.core.data.domain.batteryInfo.usecase.BatteryInfoRepositoryInteractor
import com.juanarton.core.data.domain.batteryInfo.usecase.BatteryInfoRepositoryUseCase
import com.juanarton.core.data.domain.batteryMonitoring.usecase.BatteryMonitoringRepoInteractor
import com.juanarton.core.data.domain.batteryMonitoring.usecase.BatteryMonitoringRepoUseCase
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
        batteryInfoRepositoryInteractor: BatteryInfoRepositoryInteractor
    ): BatteryInfoRepositoryUseCase

    @Binds
    @ViewModelScoped
    abstract fun provideBatteryMonitoringRepositoryUseCase(
        batteryMonitoringRepoInteractor: BatteryMonitoringRepoInteractor
    ): BatteryMonitoringRepoUseCase
}