package com.devtedi.tedi.interfaces.observer_cloudstorage

interface CloudStorageSubject {

    fun registerObserver(o : CloudStorageObserver)
    fun removeObserver(o : CloudStorageObserver)
    fun updateObserverSuccess()
    fun updateObserverFailure()
}