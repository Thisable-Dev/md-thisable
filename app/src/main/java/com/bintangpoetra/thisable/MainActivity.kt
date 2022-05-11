package com.bintangpoetra.thisable

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bintangpoetra.thisable.databinding.ActivityMainBinding
import com.bintangpoetra.thisable.utils.ext.gone
import com.bintangpoetra.thisable.utils.ext.hide
import com.bintangpoetra.thisable.utils.ext.show

class MainActivity : AppCompatActivity() {

    private lateinit var _activityMainBinding: ActivityMainBinding
    private val binding get() = _activityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_activityMainBinding.root)

        val navHostBottomBar = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navControllerBottomBar = navHostBottomBar.navController

        binding.bottomNav.setupWithNavController(navControllerBottomBar)
        navControllerBottomBar.addOnDestinationChangedListener { _, currentDestination, _ ->
            if (currentDestination.id == R.id.homeFragment || currentDestination.id == R.id.thisableFragment || currentDestination.id == R.id.profileFragment) {
                binding.bottomNav.show()
            } else {
                binding.bottomNav.gone()
            }
        }
    }

}