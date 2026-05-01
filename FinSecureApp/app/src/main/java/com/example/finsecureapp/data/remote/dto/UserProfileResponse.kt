package com.example.finsecureapp.data.remote.dto

data class UserProfileResponse(
    val id: String,
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val account: UserAccountDto
)

data class UserAccountDto(
    val id: String,
    val userId: String,
    val accountNumber: String,
    val balance: Double,
    val currency: String,
    val createdAt: String
)