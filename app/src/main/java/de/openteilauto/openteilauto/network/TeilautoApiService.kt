package de.openteilauto.openteilauto.network

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

private const val BASE_URL = "https://sal2.teilauto.net/api/"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface TeilautoApiService {
    @FormUrlEncoded
    @POST("login")
    suspend fun login(@Field("password") password: String,
                      @Field("membershipNo") membershipNo: String): String
}

object TeilautoApi {
    val retrofitService: TeilautoApiService by lazy {
        retrofit.create(TeilautoApiService::class.java)
    }
}