package com.bintangpoetra.thisable.utils

class FrameMetadata (private val width : Int, private val height : Int, private val rotation : Int ) {

    fun getWidth() :Int {
        return this.width
    }

    fun getHeight() : Int {
        return this.height
    }

    fun getRotation() : Int {
        return this.rotation
    }

    fun build(): FrameMetadata {
        return FrameMetadata(width, height, rotation)
    }
}
