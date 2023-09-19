package com.shinkevich.pexelsapp.View

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.shinkevich.pexelsapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var bottomMenu: BottomNavigationView
    lateinit var navController: NavController
    lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_main)

        bottomMenu = findViewById<BottomNavigationView>(R.id.bottomMenu)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        bottomMenu.setupWithNavController(navController)
        bottomMenu.setItemIconTintList(null)

        navController.addOnDestinationChangedListener { controller: NavController,
                                                        destination: NavDestination,
                                                        arguments: Bundle? ->

            if (destination.id == R.id.detailsFragment) {
                bottomMenu.visibility = View.GONE
            } else {
                bottomMenu.visibility = View.VISIBLE
            }
        }
    }
}
