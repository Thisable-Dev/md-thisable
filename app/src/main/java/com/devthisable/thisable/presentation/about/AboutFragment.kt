package com.devthisable.thisable.presentation.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.devthisable.thisable.databinding.FragmentAboutBinding

class AboutFragment: Fragment() {

    private var _fragmentAboutBinding: FragmentAboutBinding? = null
    private val binding get() = _fragmentAboutBinding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentAboutBinding = FragmentAboutBinding.inflate(inflater, container, false)
        return _fragmentAboutBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener {
            it.findNavController().popBackStack()
        }
    }

}