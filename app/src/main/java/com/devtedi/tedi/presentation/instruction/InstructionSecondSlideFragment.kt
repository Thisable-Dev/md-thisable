package com.devtedi.tedi.presentation.instruction

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.devtedi.tedi.databinding.FragmentInstructionSecondBinding
import com.devtedi.tedi.utils.ext.click

class InstructionSecondSlideFragment: Fragment() {

    private var _fragmentSecondSlideBinding: FragmentInstructionSecondBinding? = null
    private val binding get() = _fragmentSecondSlideBinding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentSecondSlideBinding = FragmentInstructionSecondBinding.inflate(inflater)
        return _fragmentSecondSlideBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAction()
    }

    private fun initAction() {
        binding.btnTxTalkback.click {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }
    }

}