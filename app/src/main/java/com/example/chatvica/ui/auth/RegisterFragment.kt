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
import com.example.chatvica.data.network.AuthApiService
import com.example.chatvica.data.network.RetrofitClient
import com.example.chatvica.data.storage.SecureStorage
import com.example.chatvica.data.storage.TokenManager
import com.example.chatvica.databinding.FragmentRegisterBinding
import com.example.chatvica.ui.main.MainActivity
import com.google.gson.JsonParser
import java.io.IOException
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var authService: AuthApiService // Изменено на lateinit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        authService = RetrofitClient.getAuthService(requireContext()) // Инициализация здесь
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegister.setOnClickListener {
            val login = binding.etUsername.text.toString()
            val name = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (login.isEmpty() || name.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val response = authService.register(
                        RegisterRequest(
                            login = login,
                            name = name,
                            password = password
                        )
                    )

                    if (response.isSuccessful) {
                        val token = response.body()?.token
                        if (token != null) {
                            TokenManager.saveToken(requireContext(), token)

                            // Добавляем загрузку данных пользователя
                            val apiService = RetrofitClient.getApiService(requireContext())
                            try {
                                val userResponse = apiService.getUser("Bearer $token", null)
                                if (userResponse.isSuccessful) {
                                    startActivity(Intent(requireContext(), MainActivity::class.java))
                                    requireActivity().finish()
                                } else {
                                    val errorBody = userResponse.errorBody()?.string()
                                    val errorMessage = try {
                                        JsonParser.parseString(errorBody)
                                            .asJsonObject["message"]
                                            .asString
                                    } catch (e: Exception) {
                                        "Ошибка: ${userResponse.code()}"
                                    }
                                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    requireContext(),
                                    "Ошибка загрузки пользователя: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(requireContext(), "Invalid response", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = try {
                            JsonParser.parseString(errorBody).asJsonObject["message"].asString
                        } catch (e: Exception) {
                            "Ошибка: ${response.code()}"
                        }
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    Toast.makeText(requireContext(), "Ошибка сети: ${e.message}", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}