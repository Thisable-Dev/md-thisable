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
import com.devtedi.tedi.utils.ConstVal
import com.devtedi.tedi.utils.ConstVal.KEY_EMERGENCY_CONTACT
import com.devtedi.tedi.utils.SharedPrefManager
import com.devtedi.tedi.utils.ext.click
import com.devtedi.tedi.utils.ext.showDataBottomSheet
import com.devtedi.tedi.utils.ext.showToast
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
                    saveNewEmergencyContact(phoneNumber)
                }
            })
        }
        binding.btnCall.click {
            if (pref.getEmergencyContact.isNullOrEmpty()) {
                showDataBottomSheet(listener = {
                    showToast(it)
                    if (it.isNotEmpty()) {
                        binding.tvEmergencyContact.text =
                            getString(R.string.label_emergency_contact, it)
                        phoneNumber = it
                        saveNewEmergencyContact(phoneNumber)
                    }
                })
            } else {
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel:${phoneNumber}")
                startActivity(intent)
            }
        }
    }

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

    private fun saveNewEmergencyContact(phoneNumber: String) {
        pref.updateStringPreferenceValue(KEY_EMERGENCY_CONTACT, phoneNumber)
    }

}