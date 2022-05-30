package com.devthisable.thisable.presentation.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.NavHostFragment
import com.devthisable.thisable.R
import com.devthisable.thisable.databinding.FragmentSuccessCustomDialogBinding

class FragmentSuccessCustomDialog: DialogFragment() {

    private var _fragmentSuccessCustomDialog: FragmentSuccessCustomDialogBinding? = null
    private val binding get() = _fragmentSuccessCustomDialog!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentSuccessCustomDialog = FragmentSuccessCustomDialogBinding.inflate(inflater, container, false)
        return _fragmentSuccessCustomDialog?.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _fragmentSuccessCustomDialog = FragmentSuccessCustomDialogBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(requireActivity())
            .setView(binding.root)

        initAction()
        return builder.create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAction()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    private fun initAction() {
        binding.btnWarning.setOnClickListener {
            NavHostFragment.findNavController(this).popBackStack()
        }
    }

}