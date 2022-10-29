package com.devtedi.tedi.presentation.help

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.devtedi.tedi.R
import com.devtedi.tedi.databinding.FragmentHelpBinding
import com.devtedi.tedi.utils.SharedPrefManager
import com.devtedi.tedi.utils.ext.click
import com.devtedi.tedi.utils.ext.showToast

class HelpFragment : Fragment() {

    private var _fragmentHelpBinding: FragmentHelpBinding? = null
    private val binding get() = _fragmentHelpBinding!!

    private lateinit var pref: SharedPrefManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentHelpBinding = FragmentHelpBinding.inflate(inflater, container, false)
        return _fragmentHelpBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = SharedPrefManager(requireContext())

        initAction()
    }

    private fun initAction() {
        binding.toolbar.setNavigationOnClickListener {
            it.findNavController().popBackStack()
        }

        binding.tvTediEmail.click {
            val intentToEmailApp = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:" + getString(R.string.tedi_email))
                putExtra(Intent.EXTRA_EMAIL, pref.getEmail)
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.label_subject_template))
            }
            try {
                startActivity(Intent.createChooser(intentToEmailApp, "Send mail..."))
            } catch (ex: ActivityNotFoundException) {
                showToast("Email App is not installed")
            }
        }
    }

}