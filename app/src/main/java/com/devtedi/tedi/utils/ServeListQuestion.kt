package com.devtedi.tedi.utils

import android.content.Context
import com.devtedi.tedi.R

object ServeListQuestion {

    fun  getListQuestionCurrency(context : Context): Array<String> {
        val arrList = arrayOf<String> (
            context.getString(R.string.question_1_currency_detection),
            context.getString(R.string.question_2_currency_detection),
        )
        return arrList
    }

    fun getListQuestion(context : Context) : Array<String> {
        val arrList =  arrayOf<String>(
            context.getString(R.string.question_1_obj_detection),
        )
        return arrList
    }

    fun getListQuestionText(requireContext: Context): Array<String> {
        val arrList = arrayOf<String> (
            "Teks apa yang saya tangkap ?"
                )
        return arrList
    }
}
