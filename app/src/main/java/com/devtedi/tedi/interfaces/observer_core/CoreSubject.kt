package com.devtedi.tedi.observer_core

interface CoreSubject {

    fun registerObserver(o : CoreObserver);
    fun removeObserver(o : CoreObserver);
    fun notifyObserver();

}