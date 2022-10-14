package com.devtedi.tedi.presentation.familyhelp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.devtedi.tedi.R
import com.devtedi.tedi.databinding.FragmentFamilyHelpBinding
import com.devtedi.tedi.utils.SharedPrefManager
import com.devtedi.tedi.utils.ext.click
import com.devtedi.tedi.utils.ext.showDataBottomSheet
import timber.log.Timber


class FamilyHelpFragment : Fragment() {

    private var _fragmentFamilyHelpBinding: FragmentFamilyHelpBinding? = null
    private val binding get() = _fragmentFamilyHelpBinding!!

    private lateinit var pref: SharedPrefManager

    var phoneNumber: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentFamilyHelpBinding = FragmentFamilyHelpBinding.inflate(inflater, container, false)
        return _fragmentFamilyHelpBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = SharedPrefManager(requireContext())

        askPermission()

        initUI()
        initAction()
    }

    private fun initUI() {
        binding.apply {
            phoneNumber = pref.getEmergencyContact ?: "-"
            tvEmergencyContact.text = getString(R.string.label_emergency_contact, phoneNumber)
        }
    }

    private fun initAction() {
        binding.btnEditEmergencyContact.click {
            showDataBottomSheet(listener = {
                if (it.isNotEmpty()) {
                    binding.tvEmergencyContact.text =
                        getString(R.string.label_emergency_contact, it)
                    phoneNumber = it
                }
            })
        }
        binding.btnCall.click {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:${phoneNumber}")
            startActivity(intent)
        }
    }

    /*private fun initCallProjection() {
        val projection = arrayOf(
            ContactsContract.Data._ID,
            ContactsContract.Data.DISPLAY_NAME,
            ContactsContract.Data.MIMETYPE
        )
        val resolver = context?.contentResolver

        val cursor: Cursor? = resolver?.query(
            ContactsContract.Data.CONTENT_URI,
            projection, null, null,
            ContactsContract.Contacts.DISPLAY_NAME
        )
        cursor?.let {
            val _id = it.getLong(it.getColumnIndexOrThrow(ContactsContract.Data._ID))
            val displayName =
                it.getString(it.getColumnIndexOrThrow(ContactsContract.Data.DISPLAY_NAME))
            val mimeType =
                it.getString(it.getColumnIndexOrThrow(ContactsContract.Data.MIMETYPE))

            Timber.d("<<<<<<<<<<<<< MimeType : $mimeType")

            *//*if (mimeType.equals("vnd.android.cursor.item/vnd.com.whatsapp.voip.call") || mimeType.equals(
                    "vnd.android.cursor.item/vnd.com.whatsapp.video.call"
                )
            ) {
                if (mimeType.equals("vnd.android.cursor.item/vnd.com.whatsapp.voip.call")) {
                    val voiceCallID = _id.toString();
                    val intent = Intent()
                    intent.action = Intent.ACTION_VIEW

                    intent.setDataAndType(
                        Uri.parse("content://com.android.contacts/data/$voiceCallID"),
                        "vnd.android.cursor.item/vnd.com.whatsapp.voip.call"
                    )
                    intent.setPackage("com.whatsapp")

                    startActivity(intent)
                } else {
                    val videoCallID = _id.toString();
                }
            }*//*
        }
    }*/

    private fun checkPermission(permission: Array<String>): Boolean = permission.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun askPermission() {
        val listOfPermission = arrayOf(
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.CALL_PHONE
        )
        if (!checkPermission(listOfPermission)) {
            ActivityCompat.requestPermissions(requireActivity(), listOfPermission, 1)
        }
    }

}