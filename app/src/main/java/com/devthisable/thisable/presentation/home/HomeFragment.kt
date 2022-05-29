package com.devthisable.thisable.presentation.home

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.devthisable.thisable.R
import com.devthisable.thisable.data.dummy.BannerDummy.getBannerList
import com.devthisable.thisable.databinding.FragmentHomeBinding
import com.devthisable.thisable.presentation.feature_object.ObjectDetectionFragment
import com.devthisable.thisable.utils.showToastMessage
import java.security.Permission
import java.security.Permissions
import java.util.jar.Manifest

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
        setOnClickListener()
        askPermission()
    }
    private fun checkPermission(requestPermissionLauncher : ActivityResultLauncher<String>) {
        when {
            ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED -> {
                //showToastMessage(requireContext(), "Hello")
            }
            else -> {
                val dialog: AlertDialog = AlertDialog.Builder(requireContext())
                    .setTitle("Peringatan")
                    .setMessage("Aplikasi ini menggunakan kamera gawai anda untuk dapat digunakan, Mohon Berikan Akses untuk kamera")
                    .setPositiveButton("Beri Akses", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            requestPermissionLauncher.launch( android.Manifest.permission.CAMERA )
                        }
                    })
                    .setNegativeButton("Tidak", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            showToastMessage(requireContext(), "Aplikasi Akan Ditutup")
                            requireActivity().finish()
                        }

                    }).create()
                dialog.show()
            }
        }
    }

    private fun askPermission() {
        val requestPermissionLauncher : ActivityResultLauncher<String> = registerForActivityResult(ActivityResultContracts.RequestPermission()) { is_granted : Boolean ->
            if (is_granted) {
                //
                showToastMessage(requireContext(), "Terima kasih, Sekarang kamu dapat menggunakan Aplikasi")
            }
        }
        checkPermission(requestPermissionLauncher)

    }

    private fun initUI() {
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvBanner)
        binding.rvBanner.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setOnClickListener () {

        var bundleData = Bundle()
        binding.cvDeteksiObjek.setOnClickListener {
            bundleData.putString("EXTRA_DATA",resources.getString(R.string.action_object_detection))
            findNavController().navigate(R.id.action_homeFragment_to_coreActivity,args=bundleData)
        }
        binding.cvDeteksiText.setOnClickListener {
            bundleData.putString("EXTRA_DATA",resources.getString(R.string.action_text_detection))
            findNavController().navigate(R.id.action_homeFragment_to_coreActivity,args=bundleData)
        }
        binding.cvDeteksiUang.setOnClickListener {

            bundleData.putString("EXTRA_DATA",resources.getString(R.string.action_currency_detection))
            findNavController().navigate(R.id.action_homeFragment_to_coreActivity,args=bundleData)
        }

        binding.cvPenerjemahBisindo.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_coreActivity)
        }
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