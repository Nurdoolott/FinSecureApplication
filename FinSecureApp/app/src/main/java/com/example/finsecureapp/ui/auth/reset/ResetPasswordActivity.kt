package com.example.finsecureapp.ui.auth.reset

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.finsecureapp.databinding.ActivityResetPasswordBinding

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        finish()
    }
}