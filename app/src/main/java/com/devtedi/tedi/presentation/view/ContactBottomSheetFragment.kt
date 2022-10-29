package com.devtedi.tedi.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.devtedi.tedi.R
import com.devtedi.tedi.databinding.FragmentContantBottomSheetBinding
import com.devtedi.tedi.utils.ConstVal.KEY_EMERGENCY_CONTACT
import com.devtedi.tedi.utils.SharedPrefManager
import com.devtedi.tedi.utils.ext.click
import com.devtedi.tedi.utils.ext.isPhoneNumberValid
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ContactBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var pref: SharedPrefManager

    companion object {
        fun newInstance(
            listener: ((String) -> Unit)?
        ): ContactBottomSheetFragment = ContactBottomSheetFragment().apply {
            arguments = Bundle().apply {
            }
            this.listener = listener
        }
    }

    private var listener: ((String) -> Unit)? = null

    private var _fragmentContactBottomSheetBinding: FragmentContantBottomSheetBinding? = null
    private val binding get() = _fragmentContactBottomSheetBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentContactBottomSheetBinding =
            FragmentContantBottomSheetBinding.inflate(inflater, container, false)
        return _fragmentContactBottomSheetBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = SharedPrefManager(requireContext())

        initUI()
        initAction()
    }

    private fun initUI() {
        pref.getEmergencyContact?.let {
            binding.edtPhoneNumber.setText(it)
        }
    }

    private fun initAction() {
        binding.btnSave.click {
            val phoneNumber = binding.edtPhoneNumber.text.toString()
            if (phoneNumber.isPhoneNumberValid()) {
                listener?.invoke(phoneNumber)
                dismiss()
            } else {
                binding.edtPhoneNumber.apply {
                    requestFocus()
                    error = "Nomor Kontak Darurat tidak boleh kosong"
                }
            }
        }
    }

}