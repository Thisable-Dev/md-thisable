package com.bintangpoetra.thisable.utils

import android.content.Context
import android.content.SharedPreferences
import com.bintangpoetra.thisable.utils.ConstVal.PREFS_NAME

class SharedPrefManager(context: Context) {

    private var prefs: SharedPreferences = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val editor = prefs.edit()

    fun setStringPreference(prefKey: String, value:String){
        editor.putString(prefKey, value)
        editor.apply()
    }

    fun setBooleanPreference(prefKey: String, value: Boolean){
        editor.putBoolean(prefKey, value)
        editor.apply()
    }

    fun clearPreferenceByKey(prefKey: String){
        editor.remove(prefKey)
        editor.apply()
    }

}