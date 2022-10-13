package com.devtedi.tedi.interfaces.observer_analyzer

import com.devtedi.tedi.interfaces.observer_core.CoreObserver

interface AnalyzerSubject {

    fun registerObserver(o : AnalyzerObserver);
    fun removeObserver(o : AnalyzerObserver);
    fun notifyObserver();
}