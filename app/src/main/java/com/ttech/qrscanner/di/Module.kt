package com.ttech.qrscanner.di

import android.content.Context
import androidx.room.Room
import com.ttech.qrscanner.storage.database.QrCodeResultDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object Module {
    @Singleton
    @Provides
    fun injectQrCodeResultDao(database: QrCodeResultDb) = database.qrCodeResultDao()

    @Singleton
    @Provides
    fun injectQrCodeResultDb(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context, QrCodeResultDb::class.java, "qrCodeDb"
        ).fallbackToDestructiveMigration().build()
}