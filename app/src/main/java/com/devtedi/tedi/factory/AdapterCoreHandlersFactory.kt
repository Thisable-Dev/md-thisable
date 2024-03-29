package com.devtedi.tedi.factory

import android.app.Activity
import android.widget.Toast
import com.devtedi.tedi.R
import com.devtedi.tedi.databinding.CustomToastV1Binding
import com.devtedi.tedi.interfaces.observer_core.CoreObserver
import com.devtedi.tedi.utils.RecognitionRes

class AdapterCoreHandlersFactory(
    private val context : Activity,
    private val yolOv5ModelCreator: YOLOv5ModelCreator,
) : CoreObserver {
    private var recognitionRes: ArrayList<RecognitionRes>? = null
    private var toastLayout :  CustomToastV1Binding
    private var pattern : String = "[{}]+"
    init {
        this.yolOv5ModelCreator.registerObserver(this)
        toastLayout = CustomToastV1Binding.inflate(context.layoutInflater)
    }

    /*
            Kelas ini yang handle seluruh output TEXT yang dikeluarkan dari hasil luaran model ( MODEL ) Berarti tidak berlaku ke fitur warna,
             Kayak misalnya
             - terdapat 1 Objek a, dan 2 Objek b
             - Terdapat 1 5000, 2  10rb


     */

    fun onClickObjectInfo(info: String) {
        /*
            Fungsi yang cuman nampilin custom toast message aja
            info : message yang hendak diperlihatkan
         */
        toastLayout.textCustom.setText(info)
        val toast : Toast = Toast(context)
        toast.duration = Toast.LENGTH_LONG
        toast.setView(toastLayout.root)
        toast.show()
    }

    fun onClickLongObjectDetection(info : String)
    {
        /*
            Ini untuk handler ClickLongObjectDetection Pada Fitur Object detection,
            Output : Toast dengan Unique Object Detection Feature
         */
        when(info)
        {
            context.getString(R.string.question_1_obj_detection) ->
            {
                val items = calculateUniqueItems()
                val toast : Toast = Toast(context)
                if(items.isEmpty()) {
                    toastLayout.textCustom.text = context.getString(R.string.response_no_detection_object_detection)
                    toast.duration = Toast.LENGTH_LONG
                    toast.setView(toastLayout.root)
                    toast.show()
                }
                else {
                    toastLayout.textCustom.text = context.getString(R.string.response_1_obj_detection, replacePattern(pattern,items.toString()))
                    toast.duration = Toast.LENGTH_LONG
                    toast.setView(toastLayout.root)
                    toast.show()
                }

                toast.duration = Toast.LENGTH_LONG
                toast.setView(toastLayout.root)
                toast.show()
            }
        }
    }
    private fun replacePattern(pattern:  String,text : String)  : String
    {
        /*
        Just Simple Replacer untuk pattern tertentu saja menjadi ""
         */
        return Regex(pattern).replace(text, "")
    }

    fun onClickLongCurrencyDetection(info : String) {
        /*
            Ini untuk handler ClickLongCurrencyDetection Pada Fitur Currency detection,
            Output : Toast dengan Unique Currency Detection
         */
        when(info)
        {
            context.getString(R.string.question_1_currency_detection) ->
            {
                val items = calculateUniqueItems()
                val toast : Toast = Toast(context)

                if(items.isEmpty())
                {
                    toastLayout.textCustom.text = context.getString(R.string.response_no_detection_currency_detection)
                }
                else {
                    toastLayout.textCustom.text = context.getString(R.string.response_1_currency_detection, replacePattern(pattern,items.toString()))
                }
                toast.duration = Toast.LENGTH_LONG
                toast.setView(toastLayout.root)
                toast.show()

            }
        }
    }

    fun onClickLongTextDetection(info : String) {
        /*
            Untuk Text, If ada fitur tambahan yang hendak dilakukan aja
         */
        when (info)
        {
            // Nanti
        }
    }

    fun onClickLongColorDetection(info : String) {
        /*
               Fungsi ini belum digunakan, maybe digunakan
         */
        Toast.makeText(context, recognitionRes.toString(), Toast.LENGTH_LONG).show()
    }
    override fun update_observer() {
        recognitionRes = yolOv5ModelCreator.getResult()
    }

    // For ObjectDetection Handler
    private fun calculateUniqueItems() : MutableMap<String, Int>
    {
        /*
            Untuk Get Unique Items aja
         */
        val uniqueMap = mutableMapOf<String, Int>()
        if(recognitionRes != null)
        {
            for( i in recognitionRes as ArrayList<RecognitionRes>)
            {
                if(!uniqueMap.keys.contains(i.toString()))
                {
                    uniqueMap[i.getLabelName()] = 1
                }
                else if (uniqueMap.keys.contains(i.getLabelName())){
                    val insd = uniqueMap[i.getLabelName()]
                    uniqueMap[i.getLabelName()] =  insd!!.toInt()
                }
            }
        }
        return uniqueMap
    }


    private fun calculateTotalMoney() : Int{
        val unique : MutableMap<String, Int> = calculateUniqueItems()

        var sumOfTotal : Int = 0
        for (key in unique.keys)
        {
            try {
                val key_value_int = key.toInt()
                sumOfTotal += key_value_int * unique[key] as Int
            }
            catch (e : ClassCastException)
            {
                e.printStackTrace()
            }
        }

        return sumOfTotal
    }
}