package com.example.finsecureapp.ui.auth.register

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.finsecureapp.data.local.datastore.TokenManager
import com.example.finsecureapp.data.repository.AuthRepository
import com.example.finsecureapp.databinding.ActivityRegisterBinding
import com.example.finsecureapp.ui.auth.login.LoginActivity
import com.example.finsecureapp.ui.auth.verify.VerifyOtpActivity
import com.example.finsecureapp.utils.Resource
import com.example.finsecureapp.viewmodel.AuthViewModel
import com.example.finsecureapp.viewmodel.AuthViewModelFactory
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            AuthViewModelFactory(
                AuthRepository(),
                TokenManager(applicationContext)
            )
        )[AuthViewModel::class.java]

        binding.btnRegister.setOnClickListener {
            val fullName = binding.etFullName.text.toString().trim()
            val phoneNumber = binding.etPhoneNumber.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val emailText = binding.etEmail.text.toString().trim()
            val email = if (emailText.isEmpty()) null else emailText

            if (fullName.isEmpty() || phoneNumber.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.startRegister(fullName, phoneNumber, password, email)
            }
        }

        binding.tvGoToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        observeRegister()
    }

    private fun observeRegister() {
        lifecycleScope.launch {
            viewModel.startRegisterState.collect { state ->
                when (state) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = android.view.View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressBar.visibility = android.view.View.GONE
                        Toast.makeText(this@RegisterActivity, state.data.message, Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@RegisterActivity, VerifyOtpActivity::class.java)
                        intent.putExtra("pendingRegistrationId", state.data.pendingRegistrationId)
                        startActivity(intent)
                    }

                    is Resource.Error -> {
                        binding.progressBar.visibility = android.view.View.GONE
                        Toast.makeText(this@RegisterActivity, state.message, Toast.LENGTH_LONG).show()
                    }

                    null -> Unit
                }
            }
        }
    }
}