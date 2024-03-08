package com.juanarton.core.di

import android.content.Context
import androidx.room.Room
import com.juanarton.core.data.source.local.monitoring.room.batteryHistory.BatteryHistoryDAO
import com.juanarton.core.data.source.local.monitoring.room.HistoryDatabase
import com.juanarton.core.data.source.local.monitoring.room.chargingHistory.ChargingHistoryDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HistoryDatabase {
        val passphrase: ByteArray = SQLiteDatabase.getBytes("SharkBite-Hoo-ha-ha".toCharArray())
        val factory = SupportFactory(passphrase)

        return Room.databaseBuilder(
            context,
            HistoryDatabase::class.java, "history.db"
        ).fallbackToDestructiveMigration().openHelperFactory(factory).build()
    }

    @Provides
    @Singleton
    fun provideBatteryHistoryDao(database: HistoryDatabase): BatteryHistoryDAO = database.batteryHistoryDao()

    @Provides
    @Singleton
    fun provideChargingHistoryDao(database: HistoryDatabase): ChargingHistoryDAO = database.chargingHistoryDao()
}
