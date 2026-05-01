package com.example.finsecureapp.data.remote.dto

data class ForgotPasswordResponse(
    val message: String,
    val pendingResetId: String
)