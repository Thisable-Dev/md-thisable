package com.devtedi.tedi.presentation.feature_cloud

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.devtedi.tedi.R
import com.devtedi.tedi.databinding.FragmentCloudBinding


class CloudFragment : Fragment() {

    private var _binding : FragmentCloudBinding ?= null
    private val binding : FragmentCloudBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCloudBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}