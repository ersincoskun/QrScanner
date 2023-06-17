package com.ttech.qrscanner.core.helpers

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

class PermissionHelper {
    companion object {
        fun isPermissionsGranted(permission: String, activity: Activity): Boolean {
            if (Build.VERSION.SDK_INT >= 23) {
                val isPermissionGranted = ContextCompat.checkSelfPermission(
                    activity,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
                if (!isPermissionGranted) {
                    return false
                }
            }
            return true
        }

        fun requestPermissions(
            permission: String,
            requestPermissionLauncher: ActivityResultLauncher<String>
        ) {
            requestPermissionLauncher.launch(
                permission
            )
        }
    }
}