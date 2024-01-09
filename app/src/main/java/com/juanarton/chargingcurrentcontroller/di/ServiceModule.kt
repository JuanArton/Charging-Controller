package com.juanarton.chargingcurrentcontroller.di

import android.content.Context
import com.juanarton.core.data.domain.repository.DataRepositoryInterface
import com.juanarton.core.data.repository.DataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext

class ServiceModule {

    /*@Provides
    fun provideServicesRepository(@ApplicationContext context: Context): DataRepositoryInterface {
        return DataRepository(context)
    }*/
}