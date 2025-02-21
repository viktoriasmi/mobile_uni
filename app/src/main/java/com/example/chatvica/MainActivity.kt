package com.example.chatvica

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.chatvica.databinding.ActivityMainBinding
import androidx.core.view.doOnLayout

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Use doOnLayout for a more concise solution
        binding.navHostFragment.doOnLayout {
            val navController = findNavController(R.id.nav_host_fragment)
            binding.bottomNavigation.setupWithNavController(navController)
        }

        /*  Alternative using addOnGlobalLayoutListener (Less concise)
         binding.navHostFragment.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
             override fun onGlobalLayout() {
                 binding.navHostFragment.viewTreeObserver.removeOnGlobalLayoutListener(this) // Important: Remove the listener
                 val navController = findNavController(R.id.nav_host_fragment)
                 binding.bottomNavigation.setupWithNavController(navController)
             }
         })
         */

    }
}