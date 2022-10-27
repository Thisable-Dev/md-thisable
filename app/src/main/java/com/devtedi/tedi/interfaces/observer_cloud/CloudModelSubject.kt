package com.devtedi.tedi.interfaces.observer_cloud

interface CloudModelSubject {

    fun registerObserver(o : CloudModelObserver)
    fun removeObserver(o : CloudModelObserver)
    fun notifyObserver()
    fun notifyObserverFailure()
}