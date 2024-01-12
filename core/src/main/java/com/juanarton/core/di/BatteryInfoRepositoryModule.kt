package com.juanarton.core.di

import com.juanarton.core.data.domain.batteryInfo.repository.BatteryInfoRepositoryInterface
import com.juanarton.core.data.repository.BatteryInfoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class BatteryInfoRepositoryModule {

    @Binds
    abstract fun provideBatteryInfoRepository(
        batteryInfoRepository: BatteryInfoRepository
    ): BatteryInfoRepositoryInterface
}