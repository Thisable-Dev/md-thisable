package com.devtedi.tedi.presentation.faq

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.devtedi.tedi.R
import com.devtedi.tedi.databinding.FragmentFqaBinding

class FragmentFQA: Fragment() {

    private var _fragmentQABinding: FragmentFqaBinding? = null
    private val binding get() = _fragmentQABinding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentQABinding = FragmentFqaBinding.inflate(inflater, container, false)
        return _fragmentQABinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAction()
    }

    private fun initAction() {
        binding.toolbar.setNavigationOnClickListener {
            it.findNavController().popBackStack()
        }
        binding.tvReportBug.setOnClickListener {
            it.findNavController().navigate(R.id.action_fragmentFQA_to_bugReportFragment)
        }
    }

}