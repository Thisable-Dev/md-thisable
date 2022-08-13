package com.devtedi.tedi.presentation.instruction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.devtedi.tedi.databinding.FragmentInstructionSecondBinding

class InstructionSecondSlideFragment: Fragment() {

    private var _fragmentSecondSlideBinding: FragmentInstructionSecondBinding? = null
    private val fragmentSecondSlideBinding : FragmentInstructionSecondBinding get() = _fragmentSecondSlideBinding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _fragmentSecondSlideBinding = FragmentInstructionSecondBinding.inflate(inflater, container, false)
        return fragmentSecondSlideBinding.root
    }

}