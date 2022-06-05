package com.devthisable.thisable.presentation.faq

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.devthisable.thisable.databinding.FragmentFqaBinding

class FragmentFQA: Fragment() {

    private var _fragmentQABinding: FragmentFqaBinding? = null
    private val binding get() = _fragmentQABinding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentQABinding = FragmentFqaBinding.inflate(inflater, container, false)
        return _fragmentQABinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            it.findNavController().popBackStack()
        }
    }

}