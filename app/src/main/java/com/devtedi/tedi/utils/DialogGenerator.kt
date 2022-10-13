package com.devtedi.tedi.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.devtedi.tedi.adapter.ObjectOptionsAdapter
import com.devtedi.tedi.databinding.CustomDialogCoreBinding
import com.devtedi.tedi.factory.AdapterCoreHandlersFactory
import com.devtedi.tedi.factory.YOLOv5ModelCreator

class DialogGenerator(
    context: Activity, private val listQuestions: Array<String>,
    private val typeHandler: String, yolOv5ModelCreator: YOLOv5ModelCreator,
) : DialogFragment() {
    private val dialogAdapter = AdapterCoreHandlersFactory(context, yolOv5ModelCreator)
    private lateinit var adapter: ObjectOptionsAdapter

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