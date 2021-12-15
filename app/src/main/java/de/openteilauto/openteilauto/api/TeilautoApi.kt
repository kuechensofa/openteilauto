package de.openteilauto.openteilauto.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private const val BASE_URL =
    "https://sal2.teilauto.net/api/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface TeilautoApiService {
    @FormUrlEncoded
    @POST("login")
    suspend fun login(@FieldMap(encoded = true) loginRequestBody: Map<String, String>): LoginResponse
}

object TeilautoApi {
    val teilautoService: TeilautoApiService by lazy {
        retrofit.create(TeilautoApiService::class.java)
    }
}