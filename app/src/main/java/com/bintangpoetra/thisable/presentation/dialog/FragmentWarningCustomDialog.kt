package com.bintangpoetra.thisable.presentation.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.NavHostFragment
import com.bintangpoetra.thisable.R
import com.bintangpoetra.thisable.databinding.FragmentWarningCustomDialogBinding

class FragmentWarningCustomDialog: DialogFragment() {

    private var _fragmentWarningCustomDialog: FragmentWarningCustomDialogBinding? = null
    private val binding get() = _fragmentWarningCustomDialog!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentWarningCustomDialog = FragmentWarningCustomDialogBinding.inflate(inflater, container, false)
        return _fragmentWarningCustomDialog?.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _fragmentWarningCustomDialog = FragmentWarningCustomDialogBinding.inflate(layoutInflater)
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
            NavHostFragment.findNavController(this).navigate(R.id.action_fragmentWarningCustomDialog_to_homeFragment)
        }
    }

}