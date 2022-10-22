package com.devtedi.tedi.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.devtedi.tedi.R
import com.devtedi.tedi.data.dummy.BannerDummy.getBannerList
import com.devtedi.tedi.databinding.FragmentHomeBinding
import com.devtedi.tedi.presentation.feature_cloud.CloudModel
import com.devtedi.tedi.utils.ext.click
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeFragment: Fragment() {

    private var _fragmentHomeBinding: FragmentHomeBinding? = null
    private val binding get() = _fragmentHomeBinding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return _fragmentHomeBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFirebase()
        initUI()
        loadBanner()
        initAction()

    }

    private fun initFirebase() {
        auth = Firebase.auth
    }

    private fun initUI() {
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvBanner)
        binding.rvBanner.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val name = auth.currentUser?.displayName.toString().split(" ")
        binding.tvWelcomeWithName.text = getString(R.string.label_welcome_home, name[0])
    }

    private fun initAction() {
        var bundleData = Bundle()

        binding.btnObjectDetection.click {
            bundleData.putString(EXTRA_DATA_HOME,resources.getString(R.string.action_object_detection))
            findNavController().navigate(R.id.action_homeFragment_to_coreActivity,args=bundleData)
        }
        binding.btnTextDetection.click {
            bundleData.putString(EXTRA_DATA_HOME,resources.getString(R.string.action_text_detection))
            findNavController().navigate(R.id.action_homeFragment_to_coreActivity,args=bundleData)
        }
        binding.btnCurrencyDetection.click {
            bundleData.putString(EXTRA_DATA_HOME,resources.getString(R.string.action_currency_detection))
            findNavController().navigate(R.id.action_homeFragment_to_coreActivity,args=bundleData)
        }
        binding.btnColorDetection.click{
            bundleData.putString(EXTRA_DATA_HOME, resources.getString(R.string.action_color_detection))
            findNavController().navigate(R.id.action_homeFragment_to_coreActivity, args=bundleData)
        }
        binding.btnBisindoTranslator.click {
            findNavController().navigate(R.id.action_homeFragment_to_coreActivity)
        }
        binding.btnFamilyHelp.click {
            findNavController().navigate(R.id.action_homeFragment_to_familyHelpFragment)
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

    companion object  {
        const val EXTRA_DATA_HOME : String = "EXTRA_DATA"

    }
}