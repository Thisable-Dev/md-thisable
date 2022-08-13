package com.devtedi.tedi.data.dummy

import com.devtedi.tedi.R
import com.devtedi.tedi.data.model.Banner

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