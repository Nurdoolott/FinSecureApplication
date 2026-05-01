package com.example.finsecureapp.ui.auth.reset

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.finsecureapp.data.local.datastore.TokenManager
import com.example.finsecureapp.data.repository.AuthRepository
import com.example.finsecureapp.databinding.ActivityResetPasswordBinding
import com.example.finsecureapp.ui.auth.login.LoginActivity
import com.example.finsecureapp.utils.Resource
import com.example.finsecureapp.viewmodel.AuthViewModel
import com.example.finsecureapp.viewmodel.AuthViewModelFactory
import kotlinx.coroutines.launch

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var pendingResetId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pendingResetId = intent.getStringExtra("pendingResetId").orEmpty()

        viewModel = ViewModelProvider(
            this,
            AuthViewModelFactory(
                AuthRepository(),
                TokenManager(applicationContext)
            )
        )[AuthViewModel::class.java]

        binding.btnResetPassword.setOnClickListener {
            val otpCode = binding.etOtpCode.text.toString().trim()
            val newPassword = binding.etNewPassword.text.toString().trim()

            if (otpCode.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.resetPassword(pendingResetId, otpCode, newPassword)
            }
        }

        observeResetPassword()
    }

    private fun observeResetPassword() {
        lifecycleScope.launch {
            viewModel.resetPasswordState.collect { state ->
                when (state) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = android.view.View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressBar.visibility = android.view.View.GONE
                        Toast.makeText(this@ResetPasswordActivity, state.data.message, Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@ResetPasswordActivity, LoginActivity::class.java))
                        finishAffinity()
                    }

                    is Resource.Error -> {
                        binding.progressBar.visibility = android.view.View.GONE
                        Toast.makeText(this@ResetPasswordActivity, state.message, Toast.LENGTH_LONG).show()
                    }

                    null -> Unit
                }
            }
        }
    }
}