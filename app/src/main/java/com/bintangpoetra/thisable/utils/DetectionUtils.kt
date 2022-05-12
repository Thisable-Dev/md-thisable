package com.bintangpoetra.thisable.utils

import android.app.Dialog
import android.content.Context
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bintangpoetra.thisable.R
import com.bintangpoetra.thisable.adapters.ObjectOptionAdapter
import com.bintangpoetra.thisable.interfaces.ObjectOptionInterface


fun sumTheDetectedCurrency(listOfCurrency : List<String>) : Double {
    var totalSum: Double = 0.0
    try {
        for (currency in listOfCurrency) {
            totalSum += currency.toDouble()
        }
    }
    catch (e : Exception) {
        totalSum = 100.0
    }
    return totalSum
}

fun makeItOneString(mappedItems : Map<String, Int>) : String {
    val stringBuilder = StringBuilder()
    for( content in mappedItems) {
        stringBuilder.append(content.value)
        stringBuilder.append(" ")
        stringBuilder.append(content.key)
        stringBuilder.append(",")
    }
    return stringBuilder.toString()
}

fun showToastMessage(context : Context, msg : String) {
    Toast.makeText(context ,msg, Toast.LENGTH_SHORT).show()
}

fun showAlertDialogObjDetection (context : Context, contentDialog : Array<String >, subscriberItemListener : ObjectOptionInterface) {
    val dialog : Dialog = Dialog(context)
    val adapter = ObjectOptionAdapter(contentDialog)
    adapter.setOnClickItemListener(subscriberItemListener)
    dialog.setContentView(R.layout.custom_dialog)
    val layoutManager = LinearLayoutManager(context)
    val recyclerView = dialog.findViewById(R.id.dialog_rv) as RecyclerView
    recyclerView.layoutManager = layoutManager
    recyclerView.adapter = adapter
    dialog.show()
}

fun countTheObj(list: List<String>) : MutableMap<String, Int> {
    val returned = mutableMapOf<String, Int>()
    for (content in list) {
        if (content in returned.keys) {
            returned[content] = returned[content]?.plus(1)!!
        }
        else {
            returned[content] = 1
        }
    }
    return returned
}
