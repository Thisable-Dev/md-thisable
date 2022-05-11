package com.bintangpoetra.thisable.data.dummy

import com.bintangpoetra.thisable.R
import com.bintangpoetra.thisable.data.model.Banner

object BannerDummy {

    fun getBannerList(): List<Banner> {
        return listOf(
            Banner(
                bannerImg = R.drawable.banner1
            ),
            Banner(
                bannerImg = R.drawable.banner1
            ),
            Banner(
                bannerImg = R.drawable.banner1
            )
        )
    }

}