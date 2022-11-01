package com.devtedi.tedi.utils

import android.Manifest
import android.os.Environment
import java.lang.StringBuilder
import java.util.ArrayList

object ConstVal {

    const val CLOUD_VISION_API = "https://vision.googleapis.com/v1/"

    const val API_KEY = "AIzaSyCTwipOPeZdoMZ1da64TckdrH8XUH0g_4A"

    const val PREFS_NAME = "thisable.pref"

    const val KEY_TOKEN = "key.token"
    const val KEY_USER_ID = "key.user.id"
    const val KEY_USER_NAME = "key.user.name"
    const val KEY_IS_LOGIN = "key.isLogin"
    const val KEY_EMAIL = "key.email"
    const val KEY_IS_INTRO = "key.isIntro"
    const val KEY_EMERGENCY_CONTACT = "key.emergency_contact"

    // UI Const
    const val SPLASH_DELAY_TIME = 1500L

    // LABEL
    const val TEXT_DETECTION_LABEL = "text_detection"

    // LocalModel If Exists
    const val LOCAL_MODEL_PATH_OD : String = "LocalModelObjectDetection"
    const val LOCAL_MODEL_PATH_SL : String = "LocalModelSignlanguage"
    const val LOCAL_MODEL_PATH_CD : String = "LocalModelCurrencyDetection"

    // LocalTxtPath if Exists
    const val LOCAL_LABEL_MODEL_PATH_OD : String = "LocalModelObjectDetectionLabel"
    const val LOCAL_LABEL_MODEL_PATH_SL : String = "LocalModelSignlanguageLabel"
    const val LOCAL_LABEL_MODEL_PATH_CD : String = "LocalModelCurrencyDetectionLabel"

    // Notification If Exists
    const val IS_NOTIFICATION_INITIATED : String = "notification.initiated"
    const val IS_MODEL_UPDATE : String = "notification.isupdate"

    //Path
     val ABSOLUTE_PATH : String =
        StringBuilder().apply {
            append(Environment.getDataDirectory().absolutePath)
            append("/user/")
            append("0")
            append("/com.devtedi.tedi/")
        }.toString()

    // ya

    val arrayOfPermissions : Array<String> = arrayOf(
        Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
}