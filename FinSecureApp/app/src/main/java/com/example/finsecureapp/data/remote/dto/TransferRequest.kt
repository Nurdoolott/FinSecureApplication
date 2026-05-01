package com.example.finsecureapp.data.remote.dto

data class TransferRequest(
    val transferMethod: String,
    val receiverValue: String,
    val amount: Double
)