package com.devtedi.tedi.presentation.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.devtedi.tedi.databinding.FragmentHelpBinding

class HelpFragment: Fragment() {

    private var _fragmentHelpBinding: FragmentHelpBinding? = null
    private val binding get() = _fragmentHelpBinding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentHelpBinding = FragmentHelpBinding.inflate(inflater, container, false)
        return _fragmentHelpBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            it.findNavController().popBackStack()
        }
    }

}