package com.bintangpoetra.thisable.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bintangpoetra.thisable.databinding.FragmentProfileBinding
import com.bintangpoetra.thisable.utils.ext.popTap

class ProfileFragment: Fragment() {

    private var _fragmentProfileBinding: FragmentProfileBinding? = null
    private val binding get() = _fragmentProfileBinding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentProfileBinding = FragmentProfileBinding.inflate(inflater, container, false)
        return _fragmentProfileBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAction()
    }

    private fun initAction() {
        binding.btnMyAccount.setOnClickListener {
            it.popTap()
        }
        binding.btnFqa.setOnClickListener {
            it.popTap()
        }
        binding.btnHelp.setOnClickListener {
            it.popTap()
        }
        binding.btnReportBug.setOnClickListener {
            it.popTap()
        }
        binding.btnLogout.setOnClickListener {
            it.popTap()
        }
    }

}