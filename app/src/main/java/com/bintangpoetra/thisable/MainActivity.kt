package com.bintangpoetra.thisable

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bintangpoetra.thisable.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var _activityMainBinding: ActivityMainBinding
    private val binding get() = _activityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_activityMainBinding.root)
    }

}