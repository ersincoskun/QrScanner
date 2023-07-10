package com.ttech.qrscanner.ui

import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.ttech.qrscanner.R
import com.ttech.qrscanner.core.base.BaseActivity
import com.ttech.qrscanner.core.manager.AppOpenAdManager
import com.ttech.qrscanner.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private lateinit var navController:NavController

    override fun assignObjects() {
        super.assignObjects()
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun onLayoutReady() {
        super.onLayoutReady()
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.barcodeScannerFragment, R.id.historyFragment, R.id.favoritesFragment, R.id.settingsFragment,R.id.resultFragment),binding.drawerLayout)
        binding.toolbar.setupWithNavController(navController,appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onResumed() {
        super.onResumed()
        AppOpenAdManager.loadAd(this)
        AppOpenAdManager.showAdIfAvailable(this)
    }
}