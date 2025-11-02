package com.tuempresa.aprendeapp.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.tuempresa.aprendeapp.R
import com.tuempresa.aprendeapp.databinding.ActivityMainBinding
import com.tuempresa.aprendeapp.ui.auth.LoginActivity
import com.tuempresa.aprendeapp.ui.profile.ProfileFragment
import com.tuempresa.aprendeapp.ui.resources.AddEditResourceActivity
import com.tuempresa.aprendeapp.ui.resources.ResourcesFragment
import com.tuempresa.aprendeapp.viewmodel.AuthViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!authViewModel.isLoggedIn()) {
            navigateToLogin()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()

        if (savedInstanceState == null) {
            loadFragment(ResourcesFragment())
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(ResourcesFragment())
                    true
                }
                R.id.nav_add -> {
                    startActivity(Intent(this, AddEditResourceActivity::class.java))
                    false
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                R.id.nav_logout -> {
                    showLogoutDialog()
                    false
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.menu_logout))
            .setMessage(getString(R.string.logout_confirmation))
            .setPositiveButton("Sí") { _, _ ->
                authViewModel.logout()
                navigateToLogin()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigation.selectedItemId = R.id.nav_home
    }
}