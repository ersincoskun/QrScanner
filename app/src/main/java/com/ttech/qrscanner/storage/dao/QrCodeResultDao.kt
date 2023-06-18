package com.ttech.qrscanner.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ttech.qrscanner.data.QrCodeResultData

@Dao
interface QrCodeResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQrCodeResultData(qrCodeResultData: QrCodeResultData): Long

    @Query("SELECT * FROM qrCodeResultDb")
    suspend fun getAllQrCodeResultData(): List<QrCodeResultData>?

    @Query("SELECT * FROM qrCodeResultDb WHERE primaryId = :id")
    suspend fun getQrCodeResultDataById(id: Long): QrCodeResultData?

    @Query("DELETE FROM qrCodeResultDb WHERE primaryId = :id")
    suspend fun deleteQrCodeResultDataById(id: Long)

    @Query("DELETE FROM qrCodeResultDb")
    suspend fun deleteAllQrCodeResultData()

    @Query("UPDATE qrCodeResultDb SET isFavorite = :isFavorite WHERE primaryId = :id")
    suspend fun updateIsFavorite(id: Long, isFavorite: Boolean)
}