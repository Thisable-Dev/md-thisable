package com.devthisable.thisable.presentation.instruction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.devthisable.thisable.R
import com.devthisable.thisable.databinding.FragmentInstructionBinding

class InstructionFragment: Fragment() {

    private var _fragmentInstructionBinding: FragmentInstructionBinding? = null
    private val binding get() = _fragmentInstructionBinding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentInstructionBinding = FragmentInstructionBinding.inflate(inflater, container, false)
        return _fragmentInstructionBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        initAction()
    }

    private fun initUI() {
        val vpAdapter = InstructionViewPagerAdapter(requireActivity())
        binding.vp2Instruction.adapter = vpAdapter
    }

    private fun initAction() {
        binding.btnUnderstand.setOnClickListener {
            it.findNavController().navigate(R.id.action_instructionFragment_to_homeFragment)
        }
    }

}