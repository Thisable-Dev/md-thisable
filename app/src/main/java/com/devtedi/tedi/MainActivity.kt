package com.devtedi.tedi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.devtedi.tedi.databinding.ActivityMainBinding
import com.devtedi.tedi.notifications.Notification
import com.devtedi.tedi.utils.ConstVal
import com.devtedi.tedi.utils.SharedPrefManager
import com.devtedi.tedi.utils.ext.gone
import com.devtedi.tedi.utils.ext.show
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var _activityMainBinding: ActivityMainBinding
    private lateinit var prefs : SharedPrefManager
    private val binding get() = _activityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_activityMainBinding.root)

        prefs = SharedPrefManager(this)

        val notifIsInitiated = prefs.getIsNotificationInitiated

        if (!notifIsInitiated) {
            prepareNotification()
        }

        val navHostBottomBar = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navControllerBottomBar = navHostBottomBar.navController

        binding.bottomNav.setupWithNavController(navControllerBottomBar)
        binding.bottomNav.background = null
        navControllerBottomBar.addOnDestinationChangedListener { _, currentDestination, _ ->
            if (currentDestination.id == R.id.homeFragment || currentDestination.id == R.id.coreActivity || currentDestination.id == R.id.profileFragment) {
                binding.bottomNav.show()
            } else {
                binding.bottomNav.gone()
            }
        }
    }

    private fun prepareNotification()
    {
        val workManager = WorkManager.getInstance(this)

        val periodicWorker = PeriodicWorkRequestBuilder<Notification>(1, TimeUnit.DAYS).build()

        workManager.enqueue(periodicWorker)

        prefs.setBooleanPreference(ConstVal.IS_NOTIFICATION_INITIATED, true)
    }

}