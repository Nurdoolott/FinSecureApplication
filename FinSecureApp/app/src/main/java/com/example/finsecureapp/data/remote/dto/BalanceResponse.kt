package com.example.finsecureapp.data.remote.dto

data class BalanceResponse(
    val accountNumber: String,
    val balance: Double,
    val currency: String
)