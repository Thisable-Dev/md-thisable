package com.devtedi.tedi.utils

import androidx.camera.view.PreviewView
import com.devtedi.tedi.factory.YOLOv5ModelCreator

interface FeatureBaseline {

    var yolov5TFLiteDetector: YOLOv5ModelCreator
    var cameraPreviewView: PreviewView
    var cameraProcess: CameraProcess

}