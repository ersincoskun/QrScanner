package com.ttech.qrscanner.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ttech.qrscanner.storage.dao.QrCodeResultDao
import com.ttech.qrscanner.data.QrCodeResultData

@Database(entities = [QrCodeResultData::class], version = 1)
abstract class QrCodeResultDb : RoomDatabase() {
    abstract fun qrCodeResultDao(): QrCodeResultDao
}