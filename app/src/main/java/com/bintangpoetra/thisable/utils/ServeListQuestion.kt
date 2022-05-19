package com.bintangpoetra.thisable.utils

import android.content.Context
import com.bintangpoetra.thisable.R

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
            context.getString(R.string.question_2_obj_detection),
        )
        return arrList
    }
}
