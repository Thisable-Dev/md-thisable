package com.devtedi.tedi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devtedi.tedi.data.remote.cloudmodel.CloudModelData
import com.devtedi.tedi.databinding.ItemLayoutCloudmodelBinding

/**
 *
 * Kelas ini digunakan sebagai adapter RecyclerView, yang menampilkan list CloudModelData.
 *
 * @property listCloudModel list dari cloud model yang digunakan.
 * @constructor untuk buat instance dari CloudModelAdapter.
 */
class CloudModelAdapter(
    private val listCloudModel : ArrayList<CloudModelData>,
    private val onClick : (modelName : String) -> Unit

) : RecyclerView.Adapter<CloudModelAdapter.ViewHolder>(){

    /**
     *
     * Kelas ini digunakan sebagai ViewHolder yang menampung view untuk setiap item di RecyclerView.
     *
     * @property binding class hasil generate dari viewBinding untuk R.layout.item_layout_cloudmodel.xml.
     * @constructor untuk buat instance dari ViewHolder.
     */
    inner class ViewHolder(private val binding : ItemLayoutCloudmodelBinding)  : RecyclerView.ViewHolder(binding.root)
    {
        /**
         * Fungsi untuk bind/memasukkan isi [data] ke properti yang ada pada view.
         * @param [data] data item cloud model
         */
        fun bind(data : CloudModelData)
        {
            binding.tvNamaModel.text = data.modelName
            binding.tvModelversion.text = data.modelVersion
            binding.btnStatusmodel.setOnClickListener {
                onClick(data.modelName)
            }
        }
    }

    /**
     * Fungsi untuk membuat object [CloudModelModelAdapter.ViewHolder]
     *
     * @return object dari [CloudModelModelAdapter.ViewHolder].
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CloudModelAdapter.ViewHolder {
        val inflater = ItemLayoutCloudmodelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(inflater)
    }

    /**
     * Fungsi ini akan terpanggil ketika ada item baru pada RecyclerView.
     * Sehingga Method [CloudModelAdapter.ViewHolder.bind(CloudModelData)] akan terpanggil.
     * @param [holder] ViewHolder untuk setiap View.
     * @param [position] posisi setiap item di list.
     */
    override fun onBindViewHolder(holder: CloudModelAdapter.ViewHolder, position: Int) {
        val content = listCloudModel[position]
        holder.bind(content)
    }

    /**
     * Fungsi yang dibutuhkan oleh RecyclerView untuk mengetahui jumlah data yang ada di list.
     * @return mengembalikan ukuran list data yang akan dipakai RecyclerView untuk merender item.
     */
    override fun getItemCount(): Int {
        return listCloudModel.size
    }

}