package com.devtedi.tedi.utils.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.devtedi.tedi.adapter.ObjectOptionsAdapter
import com.devtedi.tedi.databinding.CustomDialogCoreBinding
import com.devtedi.tedi.factory.AdapterCoreHandlersFactory
import com.devtedi.tedi.factory.YOLOv5ModelCreator
import com.devtedi.tedi.utils.impl_oc_ocl_color
import com.devtedi.tedi.utils.impl_oc_ocl_currency
import com.devtedi.tedi.utils.impl_oc_ocl_obj
import com.devtedi.tedi.utils.impl_oc_ocl_text

/***
 * Kelas ini pada dasarnya untuk Generate Dialog untuk Object detection dan currency untuk memberikan pilihan pada user
 *  @property listQuestions -> Basically Hanya List of questions yang hendak ditampilkan
 *  @property typeHandler -> Studi kasus apa yang hendak dilakukan dalam pembuatan dialognya
 *  @property yolov5ModelCreator --> pada dasarnya hanya model yolov5nya saja
 *  @property adapter -> Adapter untuk membuat recyclerview pada dialog
 *  @property dialogAdapter --> Kumpulan question maupun handler untuk setiap studi kasus
 */
class DialogGenerator(
    context: Activity, private val listQuestions: Array<String>,
    private val typeHandler: String, yolOv5ModelCreator: YOLOv5ModelCreator,
) : DialogFragment() {
    private val dialogAdapter = AdapterCoreHandlersFactory(context, yolOv5ModelCreator)
    private lateinit var adapter: ObjectOptionsAdapter

    /*
           Generator dialog, pada dasarnya cuman generate Dialog aja sih
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        val binding = CustomDialogCoreBinding.inflate(layoutInflater)

        binding.rvQuestions.apply {
            layoutManager = LinearLayoutManager(requireContext())
        }
        initAdapter()
        binding.rvQuestions.adapter = adapter

        alertDialog.setView(binding.root)
        return alertDialog.create().also {
            it.setCanceledOnTouchOutside(false)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(
            context: Activity, listQuestions: Array<String>,
            typeHandler: String, yolOv5ModelCreator: YOLOv5ModelCreator,
        ): DialogGenerator {
            val num = 5
            val dialogInstance =
                DialogGenerator(context, listQuestions, typeHandler, yolOv5ModelCreator)

            val args = Bundle().apply {
                putInt("num", num)
            }

            dialogInstance.arguments = args
            return dialogInstance
        }
    }

    /***
     * Fungsi ini bertujuann untuk memberitahu adapter yang akan dibentuk, dan juga handler untuk client
     * Wheter itu Object detection atau yang lainnya
     *
     */
    private fun initAdapter() {
        when (typeHandler) {
            impl_oc_ocl_obj -> {
                adapter = ObjectOptionsAdapter(listQuestions,
                    dialogAdapter::onClickObjectInfo,
                    dialogAdapter::onClickLongObjectDetection)
            }
            impl_oc_ocl_currency -> {
                adapter = ObjectOptionsAdapter(listQuestions,
                    dialogAdapter::onClickObjectInfo,
                    dialogAdapter::onClickLongCurrencyDetection
                )
            }

            impl_oc_ocl_color -> {
                adapter = ObjectOptionsAdapter(listQuestions,
                    dialogAdapter::onClickObjectInfo,
                    dialogAdapter::onClickLongColorDetection)

            }

            impl_oc_ocl_text -> {
                adapter = ObjectOptionsAdapter(listQuestions,
                    dialogAdapter::onClickObjectInfo,
                    dialogAdapter::onClickLongTextDetection)
            }
        }
    }
}