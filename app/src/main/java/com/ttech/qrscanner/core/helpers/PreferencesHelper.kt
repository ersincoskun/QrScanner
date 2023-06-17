package com.ttech.qrscanner.core.helpers

import android.content.Context
import android.util.Log
import com.ttech.qrscanner.utils.Constants.PREF_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PreferencesHelper @Inject constructor(@ApplicationContext var context: Context) {

    private var sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        const val MEMBER_TOKEN = "memberToken"
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

    /*  var isCameFromReserve: Boolean
          get() = getSharedPrefsValue(IS_CAME_FROM_RESERVE, Boolean::class.java) as Boolean
          set(value) = savePrefValue(IS_CAME_FROM_RESERVE, value)*/

}