package com.example.chatvica.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.chatvica.data.model.RegisterRequest
import com.example.chatvica.data.network.RetrofitClient
import com.example.chatvica.data.storage.SecureStorage
import com.example.chatvica.data.storage.TokenManager
import com.example.chatvica.databinding.FragmentRegisterBinding
import com.example.chatvica.ui.main.MainActivity
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val authService = RetrofitClient.authService

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()

            lifecycleScope.launch {
                try {
                    val response = authService.register(RegisterRequest(username, password))
                    if (response.isSuccessful) {
                        val token = response.body()?.token
                        if (token != null) {
                            TokenManager.saveToken(requireContext(), token)
                            startActivity(Intent(requireContext(), MainActivity::class.java))
                            requireActivity().finish()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Registration failed", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}