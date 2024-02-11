package com.juanarton.core.di

import android.content.Context
import androidx.room.Room
import com.juanarton.core.data.source.local.monitoring.room.DAO
import com.juanarton.core.data.source.local.monitoring.room.entity.TriCDatababse
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
    fun provideDatabase(@ApplicationContext context: Context): TriCDatababse {
        val passphrase: ByteArray = SQLiteDatabase.getBytes("SharkBite-Hoo-ha-ha".toCharArray())
        val factory = SupportFactory(passphrase)

        return Room.databaseBuilder(
            context,
            TriCDatababse::class.java, "3C.db"
        ).fallbackToDestructiveMigration().openHelperFactory(factory).build()
    }

    @Provides
    @Singleton
    fun provideDao(database: TriCDatababse): DAO = database.dbDao()
}
