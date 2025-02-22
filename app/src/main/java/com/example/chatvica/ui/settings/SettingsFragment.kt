package com.example.chatvica.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.chatvica.R
import com.example.chatvica.data.network.RetrofitClient
import com.example.chatvica.data.storage.SecureStorage
import com.example.chatvica.databinding.FragmentSettingsBinding
import com.example.chatvica.ui.auth.AuthActivity
import com.example.chatvica.data.network.AuthApiService
import com.example.chatvica.data.network.ApiService
import com.example.chatvica.data.storage.TokenManager
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val token = TokenManager.getToken(requireContext())
        if (token != null) {
            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.apiService.getUser("Bearer $token")
                    if (response.isSuccessful) {
                        binding.tvUserEmail.text = response.body()?.username?.let { it } ?: "No username"
                    } else {
                        Toast.makeText(requireContext(), "Failed to get user info", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: HttpException) {
                    Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnLogout.setOnClickListener {
            TokenManager.deleteToken(requireContext())

            findNavController().navigate(
                R.id.action_settings_to_auth,
                null,
                NavOptions.Builder()
                    .setPopUpTo(R.id.main_nav_graph, true)
                    .build()
            )
        }

        val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isDarkTheme = prefs.getBoolean("dark_theme", false)
        binding.switchTheme.isChecked = isDarkTheme

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            val mode = if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(mode)
            prefs.edit().putBoolean("dark_theme", isChecked).apply()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}