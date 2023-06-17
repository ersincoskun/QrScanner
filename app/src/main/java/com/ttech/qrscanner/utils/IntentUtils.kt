package com.ttech.qrscanner.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.ttech.qrscanner.core.base.BaseActivity

object IntentUtils {
    fun restartApp(activity: Activity) {
        activity.packageManager.getLaunchIntentForPackage(activity.packageName)?.let { intent ->
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            activity.startActivity(intent)
        }
    }

    fun openAppDetailSettings(activity: BaseActivity, requestCode: Int) {
        try {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", activity.packageName, null)
                activity.startActivityForResult(this, requestCode)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun openDevelopmentSettings(activity: BaseActivity) {
        Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            activity.startActivity(this)
        }
    }
}