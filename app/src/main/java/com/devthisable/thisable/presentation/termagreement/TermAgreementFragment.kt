package com.devthisable.thisable.presentation.termagreement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.devthisable.thisable.databinding.FragmentTermAgreementBinding

class TermAgreementFragment: Fragment() {

    private var _fragmentTermAgreement: FragmentTermAgreementBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentTermAgreement = FragmentTermAgreementBinding.inflate(inflater, container, false)
        return _fragmentTermAgreement?.root
    }

}