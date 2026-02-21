package com.example.bankapp.ui.main

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.bankapp.R
import com.example.bankapp.databinding.ActivityMainBinding
import com.example.bankapp.ui.main.fragments.home.addtransaction.AddTransactionDialogFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        binding.bottomNavigationView.itemIconTintList = null

        setupBottomNavigation(binding.bottomNavigationView)
        setupBackHandler()
    }

    private fun setupBottomNavigation(bottomNavigationView: BottomNavigationView) {

        bottomNavigationView.setOnItemSelectedListener { item ->

            if (item.itemId == R.id.action_add_transaction) {
                val dialog = AddTransactionDialogFragment()
                dialog.show(supportFragmentManager, "AddTransactionDialog")
                return@setOnItemSelectedListener false
            }

            if (navController.currentDestination?.id != item.itemId) {
                navController.popBackStack(R.id.homeFragment, false)
                navController.navigate(item.itemId)
            }

            true
        }
    }

    private fun setupBackHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                val current = navController.currentDestination?.id

                if (current != R.id.homeFragment) {
                    binding.bottomNavigationView.selectedItemId = R.id.homeFragment
                    navController.popBackStack(R.id.homeFragment, false)
                    navController.navigate(R.id.homeFragment)
                } else {
                    finish()
                }
            }
        })
    }
}
