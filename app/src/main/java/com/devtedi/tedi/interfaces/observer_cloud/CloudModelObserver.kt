package com.devtedi.tedi.interfaces.observer_cloud

interface CloudModelObserver {

    fun updateObserver()
    fun updateFailureObserver(message: String)

}