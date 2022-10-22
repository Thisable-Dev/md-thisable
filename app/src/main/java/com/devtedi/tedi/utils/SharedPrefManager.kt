package com.devtedi.tedi.utils

import android.content.Context
import android.content.SharedPreferences
import com.devtedi.tedi.utils.ConstVal.KEY_EMAIL
import com.devtedi.tedi.utils.ConstVal.KEY_EMERGENCY_CONTACT
import com.devtedi.tedi.utils.ConstVal.KEY_IS_INTRO
import com.devtedi.tedi.utils.ConstVal.KEY_IS_LOGIN
import com.devtedi.tedi.utils.ConstVal.KEY_TOKEN
import com.devtedi.tedi.utils.ConstVal.KEY_USER_ID
import com.devtedi.tedi.utils.ConstVal.KEY_USER_NAME
import com.devtedi.tedi.utils.ConstVal.LOCAL_MODEL_PATH_CD
import com.devtedi.tedi.utils.ConstVal.LOCAL_MODEL_PATH_OD
import com.devtedi.tedi.utils.ConstVal.LOCAL_MODEL_PATH_SL
import com.devtedi.tedi.utils.ConstVal.PREFS_NAME

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

    val getToken = prefs.getString(KEY_TOKEN, "")
    val getUserId = prefs.getString(KEY_USER_ID, "")
    val isLogin = prefs.getBoolean(KEY_IS_LOGIN, false)
    val getUserName = prefs.getString(KEY_USER_NAME, "")
    val getEmail = prefs.getString(KEY_EMAIL, "")
    val isIntro = prefs.getBoolean(KEY_IS_INTRO, false)
    val getEmergencyContact = prefs.getString(KEY_EMERGENCY_CONTACT, "");

    val getSignLanguagePath = prefs.getString(LOCAL_MODEL_PATH_SL, "");
    val getCurrencyDetectorPath = prefs.getString(LOCAL_MODEL_PATH_CD, "");
    val getObjectDetectorPath = prefs.getString(LOCAL_MODEL_PATH_OD, "")


}