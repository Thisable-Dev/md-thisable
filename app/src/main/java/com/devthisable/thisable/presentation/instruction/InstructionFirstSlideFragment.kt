package com.devthisable.thisable.presentation.instruction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.devthisable.thisable.databinding.FragmentInstructionFirstBinding

class InstructionFirstSlideFragment: Fragment(){

    private var _instructionFirsSlideFragmentBinding: FragmentInstructionFirstBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _instructionFirsSlideFragmentBinding = FragmentInstructionFirstBinding.inflate(inflater,  container, false)
        return _instructionFirsSlideFragmentBinding?.root
    }


}