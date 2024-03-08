package com.juanarton.core.di

import com.juanarton.core.data.domain.batteryMonitoring.repository.IBatteryMonitoringRepository
import com.juanarton.core.data.repository.BatteryMonitoringRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class BatteryMonitoringRepositoryModule {
    @Binds
    abstract fun provideBatteryMonitoringRepository(
        batteryMonitoringRepository: BatteryMonitoringRepository
    ): IBatteryMonitoringRepository
}
