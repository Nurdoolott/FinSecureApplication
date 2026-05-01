package com.example.finsecureapp.data.remote.dto

data class TransactionHistoryItem(
    val id: String,
    val amount: Double,
    val type: String,
    val status: String,
    val createdAt: String,
    val senderAccount: AccountNumberDto,
    val receiverAccount: AccountNumberDto
)

data class AccountNumberDto(
    val accountNumber: String
)