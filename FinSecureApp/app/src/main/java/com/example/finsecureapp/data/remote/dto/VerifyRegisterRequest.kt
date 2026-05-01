package com.example.finsecureapp.data.remote.dto

data class VerifyRegisterRequest(
    val pendingRegistrationId: String,
    val otpCode: String
)