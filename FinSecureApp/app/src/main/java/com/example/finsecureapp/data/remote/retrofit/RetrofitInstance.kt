package com.example.finsecureapp.data.remote.retrofit

import com.example.finsecureapp.data.remote.api.AccountApi
import com.example.finsecureapp.data.remote.api.AuthApi
import com.example.finsecureapp.data.remote.api.TransactionApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.finsecureapp.data.remote.api.NewsApi
import com.example.finsecureapp.data.remote.api.UserApi

object RetrofitInstance {

    private const val BASE_URL = "http://172.20.10.2:5000/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val accountApi: AccountApi by lazy {
        retrofit.create(AccountApi::class.java)
    }

    val transactionApi: TransactionApi by lazy {
        retrofit.create(TransactionApi::class.java)
    }

    val newsApi: NewsApi by lazy {
        retrofit.create(NewsApi::class.java)
    }
    val userApi: UserApi by lazy {
        retrofit.create(UserApi::class.java)
    }
}