package com.devthisable.thisable.presentation.instruction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.devthisable.thisable.databinding.FragmentInstructionSecondBinding

class InstructionSecondSlideFragment: Fragment() {

    private var _fragmentSecondSlideBinding: FragmentInstructionSecondBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentSecondSlideBinding = FragmentInstructionSecondBinding.inflate(inflater, container, false)
        return _fragmentSecondSlideBinding?.root
    }

}