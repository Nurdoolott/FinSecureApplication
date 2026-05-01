package com.example.finsecureapp.data.remote.api

import com.example.finsecureapp.data.remote.dto.BalanceResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface AccountApi {

    @GET("api/accounts/balance")
    suspend fun getBalance(
        @Header("Authorization") token: String
    ): Response<BalanceResponse>
}