package com.devtedi.tedi.custom_view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.devtedi.tedi.R

class CustomUploadImageView(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        val view = View.inflate(context, R.layout.layout_custom_upload_image_view, this)
        bindView(view)
        setAttributes(attrs)
    }

    private var tvUploadDescription: TextView? = null
    private var uploadImageContainer: LinearLayout? = null
    private var imgResult: ImageView? = null
    private var imgCamera: ImageView? = null
    private var type: Int? = null
    private var uploadDescription: String? = null

    private fun bindView(view: View) {
        tvUploadDescription = view.findViewById(R.id.tvUploadDescription)
        uploadImageContainer = view.findViewById(R.id.uploadImageContainer)
        imgCamera = view.findViewById(R.id.imgCamera)
        imgResult = view.findViewById(R.id.imgResult)
    }

    private fun setAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomUploadImageView)
        try {
            uploadDescription = typedArray.getString(R.styleable.CustomUploadImageView_upload_description)

            uploadDescription?.let {
                setDescription(it)
            }
        } finally {
            typedArray.recycle()
        }
    }

    fun setDescription(description: String) {
        tvUploadDescription?.text = description
    }

}