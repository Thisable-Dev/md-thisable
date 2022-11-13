package com.devtedi.tedi.presentation.bugreport

import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.devtedi.tedi.R
import com.devtedi.tedi.R.string
import com.devtedi.tedi.data.remote.ApiResponse
import com.devtedi.tedi.databinding.FragmentBugReportBinding
import com.devtedi.tedi.utils.*
import com.devtedi.tedi.utils.ext.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import java.io.File

@AndroidEntryPoint
class BugReportFragment : Fragment() {

    private val bugReportViewModel: BugReportViewModel by viewModels()

    private var _fragmentReportBinding: FragmentBugReportBinding? = null
    private val binding get() = _fragmentReportBinding!!

    private lateinit var auth: FirebaseAuth

    private var uploadFile: File? = null

    private var imageMultipart: MultipartBody.Part? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentReportBinding = FragmentBugReportBinding.inflate(inflater, container, false)
        return _fragmentReportBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFirebase()
        initUI()
        initAction()
        initObservers()
    }

    private fun initFirebase() {
        auth = Firebase.auth
    }

    private fun initUI() {
        binding.edtEmail.apply {
            disable()
            setText(auth.currentUser?.email)
        }
        binding.edtName.apply {
            disable()
            setText(auth.currentUser?.displayName)
        }
        binding.tvCharCounter.text = getString(string.label_report_char_counter)
        binding.toolbar.apply {
            setNavigationOnClickListener {
                it.findNavController().popBackStack()
            }
        }
    }

    private fun initAction() {
        binding.apply {
            edtReport.onTextChanged { message ->
                binding.tvCharCounter.text = message.length.toString() + "/300"
            }
            btnReport.click {
                val name = binding.edtName.text.toString().trim()
                val email = binding.edtEmail.text.toString().trim()
                val message = binding.edtReport.text.toString()
                val severity = binding.spinnerSeverity.selectedItem.toString()
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
                    uploadFile == null -> {
                        showCustomToast(getString(string.attachment_must_not_empty))
                    }
                    else -> {
                        val bodyName = name.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                        val bodyEmail = email.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                        val bodyMessage = message.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                        val bodySeverity = severity.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                        val bodyPhoneBrand = getDeviceName().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                        val bodyRam = getTotalMemories().toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                        val bodyAndroidVersion = getDeviceVersion().toRequestBody("multipart/form-data".toMediaTypeOrNull())

                        imageMultipart?.let {
                            bugReportViewModel.addNewReportBug(
                                bodyName,
                                bodyEmail,
                                bodyMessage,
                                bodySeverity,
                                bodyPhoneBrand,
                                bodyRam,
                                bodyAndroidVersion,
                                it
                            )
                        }
                    }
                }
            }
            btnAttachment.click {
                val intent = Intent()
                val mimeTypes = arrayOf(
                    "image/png", "image/jpg", "image/jpeg", "image/gif", "application/pdf"
                )
                intent.action = ACTION_GET_CONTENT
                intent.type = "*/*"
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                val chooser = Intent.createChooser(intent, "Choose a file")
                launchIntentGallery.launch(chooser)
            }
        }
    }

    private fun initObservers() {
        bugReportViewModel.addNewReportBugResult.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ApiResponse.Loading -> {
                    binding.apply {
                        showLoading(bgDim, progressBar)
                    }
                }
                is ApiResponse.Success -> {
                    binding.apply {
                        hideLoading(bgDim, progressBar)
                    }
                    clearInput()
                    findNavController().navigate(R.id.action_bugReportFragment_to_fragmentSuccessCustomDialog)
                }
                is ApiResponse.Error -> {
                    binding.apply {
                        hideLoading(bgDim, progressBar)
                    }
                    Snackbar.make(requireView(), response.errorMessage, Snackbar.LENGTH_SHORT)
                        .show()
                    Timber.e("Error <<<<<<<< ${response.errorMessage}")
                }
                else -> {
                    Timber.e(getString(string.message_unknown_state))
                }
            }
        }
    }

    private val launchIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            val file = uriToFile(selectedImg, requireContext(), selectedImg)

            val requestImageFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            imageMultipart = MultipartBody.Part.createFormData(
                "file",
                file.name,
                requestImageFile
            )

            uploadFile = file
        }
    }

    private fun clearInput() {
        binding.apply {
            edtName.clearText()
            edtEmail.clearText()
            edtReport.clearText()
            uploadFile = null
            imageMultipart = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentReportBinding = null
    }

}