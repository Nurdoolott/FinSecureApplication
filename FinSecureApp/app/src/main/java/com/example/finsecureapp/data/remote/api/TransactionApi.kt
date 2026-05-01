package com.example.finsecureapp.data.remote.api

import com.example.finsecureapp.data.remote.dto.TransactionHistoryItem
import com.example.finsecureapp.data.remote.dto.TransferRequest
import com.example.finsecureapp.data.remote.dto.TransferResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface TransactionApi {

    @POST("api/transactions/transfer")
    suspend fun transferMoney(
        @Header("Authorization") token: String,
        @Body request: TransferRequest
    ): Response<TransferResponse>

    @GET("api/transactions/history")
    suspend fun getTransactionHistory(
        @Header("Authorization") token: String
    ): Response<List<TransactionHistoryItem>>
}