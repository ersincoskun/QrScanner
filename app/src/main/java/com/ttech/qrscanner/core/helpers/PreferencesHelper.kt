package com.ttech.qrscanner.core.helpers

import android.content.Context
import android.util.Log
import com.ttech.qrscanner.utils.Constants.PREF_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PreferencesHelper @Inject constructor(@ApplicationContext private var context: Context) {

    private var sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        const val IS_VIBRATOR_ENABLE = "IS_VIBRATOR_ENABLE"
        const val IS_BEEP_ENABLE = "IS_BEEP_ENABLE"
        const val IS_AUTO_OPEN_WEB_ENABLE = "IS_AUTO_OPEN_WEB_ENABLE"
    }

    private fun getSharedPrefsValue(
        key: String,
        type: Class<*>,
        defaultLong: Long = 0,
        defaultString: String = "",
        defaultInt: Int = 0,
        defaultFloat: Float = 0f,
        defaultBoolean: Boolean = false
    ): Any? {
        return when (type) {
            String::class.java -> sharedPreferences.getString(key, defaultString)
            Long::class.java -> sharedPreferences.getLong(key, defaultLong)
            Int::class.java -> sharedPreferences.getInt(key, defaultInt)
            Float::class.java -> sharedPreferences.getFloat(key, defaultFloat)
            Boolean::class.java -> sharedPreferences.getBoolean(key, defaultBoolean)
            //Collections::class.java -> sharedPreferences.getStringSet(key, null)
            else -> {
                println("out of type")
                Log.e("TAG", "shared preferences out of type")
                null
            }
        }
    }

    private fun savePrefValue(key: String, value: Any?) {
        val editor = sharedPreferences.edit()
        if (value == null) {
            editor.remove(key)
            editor.apply()
            return
        }
        when (value) {
            is String -> editor.putString(key, value)
            is Long -> editor.putLong(key, value)
            is Int -> editor.putInt(key, value)
            is Float -> editor.putFloat(key, value)
            is Boolean -> editor.putBoolean(key, value)
        }
        editor.apply()
    }

    fun removeValue(key: String) {
        val editor = sharedPreferences.edit()
        editor.remove(key).apply()
    }

    var isVibratorEnable: Boolean
        get() = getSharedPrefsValue(IS_VIBRATOR_ENABLE, Boolean::class.java) as Boolean
        set(value) = savePrefValue(IS_VIBRATOR_ENABLE, value)

    var isBeepEnable: Boolean
        get() = getSharedPrefsValue(IS_BEEP_ENABLE, Boolean::class.java) as Boolean
        set(value) = savePrefValue(IS_BEEP_ENABLE, value)

    var isAutoOpenWebEnable: Boolean
        get() = getSharedPrefsValue(IS_AUTO_OPEN_WEB_ENABLE, Boolean::class.java) as Boolean
        set(value) = savePrefValue(IS_AUTO_OPEN_WEB_ENABLE, value)

}