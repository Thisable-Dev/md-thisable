package com.devthisable.thisable.presentation.bugreport

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.devthisable.thisable.R.string
import com.devthisable.thisable.data.remote.ApiResponse
import com.devthisable.thisable.data.source.report.ReportBugBody
import com.devthisable.thisable.databinding.FragmentBugReportBinding
import com.devthisable.thisable.utils.ext.gone
import com.devthisable.thisable.utils.ext.show
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class BugReportFragment : Fragment() {

    private val bugReportViewModel: BugReportViewModel by viewModels()

    private var _fragmentReportBinding: FragmentBugReportBinding? = null
    private val binding get() = _fragmentReportBinding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentReportBinding = FragmentBugReportBinding.inflate(inflater, container, false)
        return _fragmentReportBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFirebase()
        initUI()
        initAction()
    }

    private fun initFirebase() {
        auth = Firebase.auth
    }

    private fun initUI() {
        binding.edtEmail.setText(auth.currentUser?.email)
        binding.tvCharCounter.text = getString(string.label_report_char_counter)
        binding.toolbar.apply {
            setNavigationOnClickListener {
                it.findNavController().popBackStack()
            }
        }
    }

    private fun initAction() {
        binding.edtReport.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.tvCharCounter.text = char?.length.toString() + "/300"
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        binding.btnReport.setOnClickListener {
            val name = binding.edtName.text.toString().trim()
            val email = binding.edtEmail.text.toString().trim()
            val message = binding.edtReport.text.toString()
            when {
                name.isBlank() -> {
                    binding.edtName.apply {
                        requestFocus()
                        error = context.getString(string.message_must_not_null)
                    }
                }
                email.isBlank() -> {
                    binding.edtEmail.apply {
                        requestFocus()
                        error = context.getString(string.message_email_not_null)
                    }
                }
                message.isBlank() -> {
                    binding.edtReport.apply {
                        requestFocus()
                        error = context.getString(string.message_report_must_not_null)
                    }
                }
                else -> {
                    val reportBody = ReportBugBody(
                        nama = name,
                        email = email,
                        isi = message
                    )

                    addNewReportBug(reportBody)
                }
            }
        }
    }

    private fun addNewReportBug(reportBugBody: ReportBugBody) {
        bugReportViewModel.addNewReportBug(reportBugBody).observe(viewLifecycleOwner) { response ->
            when (response) {
                is ApiResponse.Loading -> {
                    binding.progressBar.show()
                    binding.bgDim.show()
                }
                is ApiResponse.Success -> {
                    binding.bgDim.gone()
                    binding.progressBar.gone()
                    Snackbar.make(requireView(), getString(string.message_report_success), Snackbar.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                is ApiResponse.Error -> {
                    binding.bgDim.gone()
                    binding.progressBar.gone()
                    Snackbar.make(requireView(), response.errorMessage, Snackbar.LENGTH_SHORT).show()
                }
                else -> {
                    Timber.e(getString(string.message_unknown_state))
                }
            }
        }
    }
}