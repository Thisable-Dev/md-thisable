package com.devtedi.tedi.interfaces.observer_keyboard

interface KeyboardSubject {

    fun registerObserver(o : KeyboardObserver)
    fun removeObserver(o : KeyboardObserver)

    fun notifyObserver()

}