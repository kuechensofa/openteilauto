package de.openteilauto.openteilauto.api.nominatim

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://nominatim.openstreetmap.org/"

interface NominatimApiService {
    @GET("search")
    suspend fun search(
        @Query("q") q: String,
        @Query("format") format: String = "json"
    ): List<GeoSearchResponse>

    @GET("reverse")
    suspend fun reverse(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("format") format: String = "json"
    ): ReverseGeoResponse
}

class NominatimApi {
    companion object {
        @Volatile
        private var INSTANCE: NominatimApiService? = null

        fun getInstance() =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildNominatimService().also { INSTANCE = it }
            }

        private fun buildNominatimService(): NominatimApiService {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val retrofit = Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .build()

            return retrofit.create(NominatimApiService::class.java)
        }
    }
}