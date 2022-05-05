package com.bintangpoetra.thisable.base

import android.app.Application
import timber.log.Timber

class ThisableApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }

}