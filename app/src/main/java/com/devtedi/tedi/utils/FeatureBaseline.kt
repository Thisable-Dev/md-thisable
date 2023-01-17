package com.devtedi.tedi.utils

import androidx.camera.view.PreviewView
import com.devtedi.tedi.factory.YOLOv5ModelCreator

/*
Abstraksi untuk Setiap Fitur yang menggunakan Kamera, YOLOv5 dan juga preview View
 */
interface FeatureBaseline {

    var yolov5TFLiteDetector: YOLOv5ModelCreator
    var cameraPreviewView: PreviewView
    var cameraProcess: CameraProcess

}