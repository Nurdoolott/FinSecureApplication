package com.example.finsecureapp.data.remote.dto

data class ResetPasswordRequest(
    val pendingResetId: String,
    val otpCode: String,
    val newPassword: String
)