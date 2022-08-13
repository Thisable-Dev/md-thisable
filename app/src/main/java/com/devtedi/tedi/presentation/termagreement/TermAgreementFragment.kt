package com.devtedi.tedi.presentation.termagreement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.devtedi.tedi.databinding.FragmentTermAgreementBinding

class TermAgreementFragment: Fragment() {

    private var _fragmentTermAgreement: FragmentTermAgreementBinding? = null
    private val binding get() = _fragmentTermAgreement!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentTermAgreement = FragmentTermAgreementBinding.inflate(inflater, container, false)
        return _fragmentTermAgreement?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener {
            it.findNavController().popBackStack()
        }
    }

}