package com.example.finsecureapp.data.repository

import com.example.finsecureapp.data.remote.dto.TransactionHistoryItem
import com.example.finsecureapp.data.remote.dto.TransferRequest
import com.example.finsecureapp.data.remote.dto.TransferResponse
import com.example.finsecureapp.data.remote.retrofit.RetrofitInstance
import com.example.finsecureapp.utils.ErrorUtils
import com.example.finsecureapp.utils.Resource

class TransactionRepository {

    suspend fun transfer(
        token: String,
        transferMethod: String,
        receiverValue: String,
        amount: Double
    ): Resource<TransferResponse> {
        return try {
            val response = RetrofitInstance.transactionApi.transferMoney(
                "Bearer $token",
                TransferRequest(
                    transferMethod = transferMethod,
                    receiverValue = receiverValue,
                    amount = amount
                )
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(
                    ErrorUtils.extractMessage(response.errorBody()?.string())
                )
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getHistory(token: String): Resource<List<TransactionHistoryItem>> {
        return try {
            val response = RetrofitInstance.transactionApi.getTransactionHistory("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.errorBody()?.string() ?: "Failed to load history")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }
}