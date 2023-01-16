package com.devtedi.tedi.presentation.feature_cloud

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.devtedi.tedi.R
import com.devtedi.tedi.adapter.CloudModelAdapter
import com.devtedi.tedi.data.remote.cloudmodel.CloudModelData
import com.devtedi.tedi.databinding.FragmentCloudBinding

/***
 * This class is not used
 */
class CloudFragment : Fragment() {

    private var _binding : FragmentCloudBinding ?= null
    private val binding : FragmentCloudBinding get() = _binding!!

    private lateinit var arrays : Array<String>
    private var arrayOfModels : ArrayList<CloudModelData> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCloudBinding.inflate(inflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initiateArray()
        initiateReyclerview()
    }

    private fun downloadModel(modelName : String)
    {
        when(modelName)
        {
            resources.getString(R.string.signLanguageModelCloud) ->
            {
                CloudModel.downloadSignLanguageModel()
            }
            resources.getString(R.string.currencyModelCloud) ->
            {

                CloudModel.downloadCurrencyDetectionModel()
            }
            resources.getString(R.string.objectDetectorModelCloud) ->
            {
                Toast.makeText(requireContext(), "Downloading Object Detection Model", Toast.LENGTH_SHORT).show()
                CloudModel.downloadObjectDetectionModel()
            }
        }
    }

    private fun initiateArray()
    {
        arrays = resources.getStringArray(R.array.ModelArray)

        for (strNameModel in arrays)
        {
            val dataModels = CloudModelData(
                strNameModel,
                "1.0.0",
                "latest"
            )
            arrayOfModels.add(dataModels)
        }
    }

    private fun initiateReyclerview()
    {
        val linearLayout = LinearLayoutManager(requireContext())
        val adapter = CloudModelAdapter(arrayOfModels,::downloadModel)
        binding.rvCloudmodel.layoutManager = linearLayout
        binding.rvCloudmodel.adapter = adapter
    }

}