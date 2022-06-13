package com.devthisable.thisable.data.dummy

import com.devthisable.thisable.R
import com.devthisable.thisable.data.model.Banner

object BannerDummy {

    fun getBannerList(): List<Banner> {
        return listOf(
            Banner(
                bannerImg = R.drawable.thisable_banner
            ),
            Banner(
                bannerImg = R.drawable.thisable_banner
            ),
            Banner(
                bannerImg = R.drawable.thisable_banner
            )
        )
    }

}