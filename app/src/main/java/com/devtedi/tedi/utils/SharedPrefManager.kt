package com.devtedi.tedi.utils

import android.content.Context
import android.content.SharedPreferences
import com.devtedi.tedi.utils.ConstVal.IS_MODEL_UPDATE
import com.devtedi.tedi.utils.ConstVal.IS_NOTIFICATION_INITIATED
import com.devtedi.tedi.utils.ConstVal.KEY_EMAIL
import com.devtedi.tedi.utils.ConstVal.KEY_EMERGENCY_CONTACT
import com.devtedi.tedi.utils.ConstVal.KEY_IS_INTRO
import com.devtedi.tedi.utils.ConstVal.KEY_IS_LOGIN
import com.devtedi.tedi.utils.ConstVal.KEY_TOKEN
import com.devtedi.tedi.utils.ConstVal.KEY_USER_ID
import com.devtedi.tedi.utils.ConstVal.KEY_USER_NAME
import com.devtedi.tedi.utils.ConstVal.LOCAL_LABEL_MODEL_PATH_CD
import com.devtedi.tedi.utils.ConstVal.LOCAL_LABEL_MODEL_PATH_OD
import com.devtedi.tedi.utils.ConstVal.LOCAL_LABEL_MODEL_PATH_SL
import com.devtedi.tedi.utils.ConstVal.LOCAL_MODEL_PATH_CD
import com.devtedi.tedi.utils.ConstVal.LOCAL_MODEL_PATH_OD
import com.devtedi.tedi.utils.ConstVal.LOCAL_MODEL_PATH_SL
import com.devtedi.tedi.utils.ConstVal.PREFS_NAME

/**
 *
 * Kelas untuk Penyimpanan pada apptedi
 *  @property prefs : get the current context SharedPrefences
 *  @property editor : untuk edit preferences aja
 */
class SharedPrefManager(context: Context) {

    private var prefs: SharedPreferences = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val editor = prefs.edit()

    /**
     * Untuk Set String type preferenceKey
     * @param [prefKey] Nama Keynya , [value] -> Nilai yang hendak kamu simpan
     */
    fun setStringPreference(prefKey: String, value:String){
        editor.putString(prefKey, value)
        editor.apply()
    }

    /**
     * Untuk Update String type preferenceKey
     * @param [prefKey] Nama Keynya , [value] -> Nilai yang hendak kamu simpan
     */
    fun updateStringPreferenceValue(prefKey: String, value: String) {
        editor.remove(prefKey)
        editor.putString(prefKey, value)
        editor.apply()
    }

    /**
     * Untuk Set Boolean type preferenceKey
     * @param [prefKey] Nama Keynya , [value] -> Nilai yang hendak kamu simpan
     */
    fun setBooleanPreference(prefKey: String, value: Boolean){
        editor.putBoolean(prefKey, value)
        editor.apply()
    }

    /**
     * Untuk ClearThePreferenceKey
     * @param [prefKey] Nama Keynya
     */

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

    // ModelPath
    val getSignLanguagePath = prefs.getString(LOCAL_MODEL_PATH_SL, "");
    val getCurrencyDetectorPath = prefs.getString(LOCAL_MODEL_PATH_CD, "");
    val getObjectDetectorPath = prefs.getString(LOCAL_MODEL_PATH_OD, "")

    // LabelPath
    val getSignLanguageLabelPath = prefs.getString(LOCAL_LABEL_MODEL_PATH_SL, "")
    val getObjectDetectorLabelPath = prefs.getString(LOCAL_LABEL_MODEL_PATH_OD, "")
    val getCurrencyDetectorLabelPath = prefs.getString(LOCAL_LABEL_MODEL_PATH_CD, "")

    // Notification
    val getIsNotificationInitiated = prefs.getBoolean(IS_NOTIFICATION_INITIATED, false)
    val getIsModelUpdate = prefs.getBoolean(IS_MODEL_UPDATE, false)

}