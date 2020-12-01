package com.alome.currencycoverter.Utils

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.*

interface ApiService {

    @GET("/api/{date}")
    suspend fun getResult(
        @Path("date") date: String,
        @Query("access_key") access_key: String?,
        @Query("symbols") symbols: String
    ): Response<JsonObject>


}