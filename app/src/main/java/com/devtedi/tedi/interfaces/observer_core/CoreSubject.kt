package com.devtedi.tedi.interfaces.observer_core

interface CoreSubject {

    fun registerObserver(o : CoreObserver);
    fun removeObserver(o : CoreObserver);
    fun notifyObserver();

}