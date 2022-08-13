package com.devtedi.tedi.presentation.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.devtedi.tedi.data.model.Banner
import com.devtedi.tedi.databinding.ItemLayoutBannerBinding

class BannerAdapter(private val context: Context, private val bannerList: List<Banner>): RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerAdapter.BannerViewHolder {
        val binding = ItemLayoutBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BannerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BannerAdapter.BannerViewHolder, position: Int) {
        bannerList[position].let {
            holder.bind(it)
        }
    }

    override fun getItemCount(): Int = bannerList.size

    inner class BannerViewHolder(private val binding: ItemLayoutBannerBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(banner: Banner) {
            binding.apply {
                imgBanner.setImageDrawable(AppCompatResources.getDrawable(context, banner.bannerImg))
            }
        }
    }

}