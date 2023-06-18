package com.ttech.qrscanner.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qrCodeResultDb")
data class QrCodeResultData(
    val result: String,
    var isFavorite: Boolean,
    val isQr: Boolean,
    val isUrl: Boolean,
    val date: String,
    @PrimaryKey(autoGenerate = true)
    var primaryId: Long? = null
)