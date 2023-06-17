package com.ttech.qrscanner.utils

import android.content.Context
import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.ttech.qrscanner.R
import com.ttech.qrscanner.utils.Constants.QR_SCANNER_LOG_TAG
import java.text.NumberFormat
import java.util.*

fun printLog(logText: String) {
    Log.d(QR_SCANNER_LOG_TAG, logText)
}

fun printErrorLog(logText: String) {
    Log.e(QR_SCANNER_LOG_TAG, logText)
}

fun trimGsm(gsm: String) = gsm.drop(4)

fun showErrorSnackBar(view: View, context: Context?) {
    context?.let { safeContext ->
        Snackbar.make(view, safeContext.getString(R.string.default_error_text), 2500)
            .show()
    }
}

fun formatDouble(value: Double): String {
    val format = NumberFormat.getInstance(Locale("tr", "TR")).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 2
    }
    return format.format(value)
}

