package com.example.finsecureapp.data.remote.dto

data class TransferResponse(
    val message: String,
    val transaction: TransferTransactionDto,
    val senderBalance: Double
)

data class TransferTransactionDto(
    val id: String,
    val amount: Double,
    val type: String,
    val status: String,
    val createdAt: String,
    val receiverValue: String,
    val transferMethod: String
)