package com.devtedi.tedi.factory

import android.app.Activity
import android.widget.Toast
import com.devtedi.tedi.R
import com.devtedi.tedi.databinding.CustomToastV1Binding
import com.devtedi.tedi.observer_core.CoreObserver
import com.devtedi.tedi.utils.RecognitionRes

class AdapterCoreHandlersFactory(
    private val context : Activity,
    private val yolOv5ModelCreator: YOLOv5ModelCreator,
) : CoreObserver {
    private var recognitionRes: ArrayList<RecognitionRes>? = null

    init {
        this.yolOv5ModelCreator.registerObserver(this)
        Toast.makeText(context, "Registered", Toast.LENGTH_LONG).show()
    }

    fun onClickObjectInfo(info: String) {

        val inflater = context.layoutInflater
        val toastLayout : CustomToastV1Binding = CustomToastV1Binding.inflate(inflater)
        toastLayout.textCustom.setText(info)

        val toast : Toast = Toast(context)
        // toast.setGravity(Gravity.BOTTOM, 0,0);
        toast.duration = Toast.LENGTH_LONG
        toast.setView(toastLayout.root)
        toast.show()
        //Toast.makeText(context, info, Toast.LENGTH_LONG).show()
    }

    fun onClickLongObjectDetection(info : String)
    {
        when(info)
        {
            context.getString(R.string.obj_detection_question_1) ->
            {
                val items = calculateUniqueItems()
                Toast.makeText(context, items.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

    fun onClickLongCurrencyDetection(info : String) {
        Toast.makeText(context, recognitionRes.toString(), Toast.LENGTH_LONG).show()
    }

    fun onClickLongTextDetection(info : String) {
        when (info)
        {
            context.getString(R.string.currency_question_1) -> {}
            context.getString(R.string.currency_question_2) ->
            {
                Toast.makeText(context, calculateTotalMoney(), Toast.LENGTH_LONG).show()
            }
        }
    }

    fun onClickLongColorDetection(info : String) {
        Toast.makeText(context, recognitionRes.toString(), Toast.LENGTH_LONG).show()
    }
    override fun update_observer() {
        recognitionRes = yolOv5ModelCreator.getResult()
    }

    // For ObjectDetection Handler
    private fun calculateUniqueItems() : MutableMap<String, Int>
    {

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