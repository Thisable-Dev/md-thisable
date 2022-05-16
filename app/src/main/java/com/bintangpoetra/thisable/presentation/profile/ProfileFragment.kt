package com.bintangpoetra.thisable.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bintangpoetra.thisable.R
import com.bintangpoetra.thisable.R.string
import com.bintangpoetra.thisable.databinding.FragmentProfileBinding
import com.bintangpoetra.thisable.utils.ConstVal.KEY_EMAIL
import com.bintangpoetra.thisable.utils.ConstVal.KEY_IS_LOGIN
import com.bintangpoetra.thisable.utils.ConstVal.KEY_TOKEN
import com.bintangpoetra.thisable.utils.ConstVal.KEY_USER_ID
import com.bintangpoetra.thisable.utils.ConstVal.KEY_USER_NAME
import com.bintangpoetra.thisable.utils.SharedPrefManager
import com.bintangpoetra.thisable.utils.ext.popTap

class ProfileFragment : Fragment() {

    private var _fragmentProfileBinding: FragmentProfileBinding? = null
    private val binding get() = _fragmentProfileBinding!!

    private lateinit var pref: SharedPrefManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentProfileBinding = FragmentProfileBinding.inflate(inflater, container, false)
        return _fragmentProfileBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = SharedPrefManager(requireContext())

        initUI()
        initAction()
    }

    private fun initUI() {
        binding.tvFullName.text = pref.getUserName
        binding.tvFullEmail.text = pref.getEmail
    }

    private fun initAction() {
        binding.btnMyAccount.setOnClickListener {
            it.popTap()
            it.findNavController().navigate(R.id.action_profileFragment_to_detailProfileFragment)
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
            showLogoutDialog()
        }
    }

    private fun logout() {
        pref.apply {
            clearPreferenceByKey(KEY_USER_ID)
            clearPreferenceByKey(KEY_TOKEN)
            clearPreferenceByKey(KEY_USER_NAME)
            clearPreferenceByKey(KEY_IS_LOGIN)
            clearPreferenceByKey(KEY_EMAIL)
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(getString(R.string.message_information))
            setMessage(getString(string.message_logout_confirmation))
            setPositiveButton("OK") { _, _ ->
                try {
                    logout()
                } finally {
                    findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
                }
            }
            setNegativeButton("Batal") { p0, _ ->
                p0.dismiss()
            }
        }.create().show()
    }
}