package com.devthisable.thisable.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.devthisable.thisable.data.dummy.BannerDummy.getBannerList
import com.devthisable.thisable.databinding.FragmentHomeBinding

class HomeFragment: Fragment() {

    private var _fragmentHomeBinding: FragmentHomeBinding? = null
    private val binding get() = _fragmentHomeBinding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return _fragmentHomeBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        loadBanner()
    }

    private fun initUI() {
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvBanner)
        binding.rvBanner.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun loadBanner() {
        val adapter = BannerAdapter(requireContext(), getBannerList())
        binding.rvBanner.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentHomeBinding = null
    }

}