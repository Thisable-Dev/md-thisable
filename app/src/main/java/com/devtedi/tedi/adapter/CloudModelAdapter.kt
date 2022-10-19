package com.devtedi.tedi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devtedi.tedi.data.remote.cloudmodel.CloudModelData
import com.devtedi.tedi.databinding.ItemLayoutCloudmodelBinding

class CloudModelAdapter(
    private val listCloudModel : Array<CloudModelData>, private val onClick : (value : String) -> Unit

) : RecyclerView.Adapter<CloudModelAdapter.ViewHolder>(){

    inner class ViewHolder(private val binding : ItemLayoutCloudmodelBinding)  : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(data : CloudModelData)
        {
            binding.tvNamaModel.text = data.modelName
            binding.tvModelversion.text = data.modelVersion
            binding.btnStatusmodel.setText(data.modelStatus)
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CloudModelAdapter.ViewHolder {
        val inflater = ItemLayoutCloudmodelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: CloudModelAdapter.ViewHolder, position: Int) {
        val content = listCloudModel[position]
        holder.bind(content)
    }

    override fun getItemCount(): Int {
        return listCloudModel.size
    }
}