package com.devtedi.tedi.utils

import android.util.Log
import com.devtedi.tedi.presentation.feature_cloud.CloudModel
import com.devtedi.tedi.presentation.feature_cloud.CloudStorage
import java.io.File

// Set ML Models file path to preference
fun setModelPreference(prefs: SharedPrefManager) {
    Log.d("DEBUGTAGSGAN", CloudModel.fileSignLanguage.toString())
    Log.d("DEBUGTAGSGAN", CloudModel.fileObjectDetection.toString())
    Log.d("DEBUGTAGSGAN", CloudModel.fileCurrencyDetection.toString())
    if (CloudModel.fileObjectDetection != null)
        prefs.setStringPreference(ConstVal.LOCAL_MODEL_PATH_OD,
            CloudModel.fileObjectDetection!!.path as String)

    if (CloudModel.fileCurrencyDetection != null)
        prefs.setStringPreference(ConstVal.LOCAL_MODEL_PATH_CD,
            CloudModel.fileCurrencyDetection!!.path as String)

    if (CloudModel.fileSignLanguage != null)
        prefs.setStringPreference(ConstVal.LOCAL_MODEL_PATH_SL,
            CloudModel.fileSignLanguage!!.path as String)
}

// Set ML labels file path to preference
fun setLabelsPreference(prefs: SharedPrefManager) {
    if (!CloudStorage.labelFileCurrencyDetection.isNullOrEmpty()) {
        prefs.setStringPreference(ConstVal.LOCAL_LABEL_MODEL_PATH_CD,
            CloudStorage.labelFileObjectDetection as String)
    }

    if (!CloudStorage.labelFileObjectDetection.isNullOrEmpty()) {
        prefs.setStringPreference(ConstVal.LOCAL_LABEL_MODEL_PATH_OD,
            CloudStorage.labelFileObjectDetection as String)
    }

    if (!CloudStorage.labelFileSignLanguage.isNullOrEmpty()) {
        prefs.setStringPreference(ConstVal.LOCAL_LABEL_MODEL_PATH_SL,
            CloudStorage.labelFileSignLanguage as String)
    }

}

// Set whether model need update or not to preference
fun setUpdateModelPreference(prefs: SharedPrefManager) {
    prefs.setBooleanPreference(ConstVal.IS_MODEL_UPDATE, false)
}

// Check all models are downloaded or not
fun isAllModelDownloaded(prefs: SharedPrefManager): Boolean {
    if (prefs.getSignLanguagePath.isNullOrEmpty() &&
        prefs.getObjectDetectorPath.isNullOrEmpty() &&
        prefs.getCurrencyDetectorPath.isNullOrEmpty()
    ) return false
    return true
}

// Check all labels are downloaded or not
fun isAllLabelDownloaded(prefs: SharedPrefManager): Boolean {
    if (prefs.getSignLanguageLabelPath.isNullOrEmpty() &&
        prefs.getObjectDetectorLabelPath.isNullOrEmpty() &&
        prefs.getCurrencyDetectorLabelPath.isNullOrEmpty()
    ) return false

    return true
}

// Check whether cloud model & cloud storage setup is successful already
fun setupTheCloudModelStorage(pref: SharedPrefManager): Boolean {
    if (isSetupCloudModelSuccessful(pref) && isSetupCloudStorageSuccessful(pref))
        return true

    return false
}

fun isSetupCloudStorageSuccessful(pref: SharedPrefManager): Boolean {

    if (!pref.getSignLanguageLabelPath.isNullOrEmpty() && !pref.getObjectDetectorLabelPath.isNullOrEmpty() &&
        !pref.getCurrencyDetectorLabelPath.isNullOrEmpty()
    ) {
        CloudStorage.labelFileObjectDetection = pref.getObjectDetectorLabelPath
        CloudStorage.labelFileSignLanguage = pref.getSignLanguageLabelPath
        CloudStorage.labelFileCurrencyDetection = pref.getCurrencyDetectorLabelPath
        return true
    }
    return false
}

fun isSetupCloudModelSuccessful(pref: SharedPrefManager): Boolean {
    if (!pref.getSignLanguagePath.isNullOrEmpty() && !pref.getObjectDetectorPath.isNullOrEmpty() && !pref.getSignLanguagePath.isNullOrEmpty()) {
        CloudModel.fileSignLanguage = File(pref.getSignLanguagePath as String)
        CloudModel.fileCurrencyDetection = File(pref.getCurrencyDetectorPath as String)
        CloudModel.fileObjectDetection = File(pref.getObjectDetectorPath as String)
        return true
    }
    return false
}
