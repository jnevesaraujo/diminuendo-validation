package com.example.damfp.data.remote

import retrofit2.http.GET

/**
 * Network contract (Retrofit). Document endpoints in docs/09.
 * Replace with the real endpoint of your backend / AI service.
 */
interface SampleApi {
    @GET("items")
    suspend fun getItems(): List<SampleDto>
}
