package com.elsawu.medicationevent.httputils

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object HTTPServiceBuilder {

    private const val BASE_URL =
        "https://s3-us-west-2.amazonaws.com/ph-svc-mobile-interview-jyzi2gyja/"
    private val client = OkHttpClient.Builder().build()
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(client)
        .build()

    fun <T> buildService(service: Class<T>): T {
        return retrofit.create(service)
    }
}
