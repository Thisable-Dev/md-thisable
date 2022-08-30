package com.devtedi.tedi.presentation.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.devtedi.tedi.databinding.FragmentTextDetectionResultDialogBinding
import com.devtedi.tedi.utils.ext.click
import com.devtedi.tedi.utils.ext.copyToClipBoard

class TextDetectionResultDialogFragment : DialogFragment() {

    companion object {
        fun newInstance(
            text: String
        ): TextDetectionResultDialogFragment =
            TextDetectionResultDialogFragment().apply {
                arguments = Bundle().apply {}
                this.text = text
        }
    }

    private var text: String? = null

    private var _fragmentTextDetectionResultDialogBinding: FragmentTextDetectionResultDialogBinding? = null
    private val binding get() = _fragmentTextDetectionResultDialogBinding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentTextDetectionResultDialogBinding = FragmentTextDetectionResultDialogBinding.inflate(inflater)
        return _fragmentTextDetectionResultDialogBinding?.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _fragmentTextDetectionResultDialogBinding = FragmentTextDetectionResultDialogBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(requireActivity())
            .setView(binding.root)

        initAction()
        initUI()

        return builder.create()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    private fun initUI() {
        binding.tvDetectionResult.movementMethod = ScrollingMovementMethod()
        text?.let { result ->
            binding.tvDetectionResult.text = result
        }
    }

    private fun initAction() {
        binding.btnCopyText.click {
            text?.let {
                context?.copyToClipBoard(it.toString())
            }
        }
        binding.btnRedetect.click {
            dialog?.dismiss()
        }
    }
}