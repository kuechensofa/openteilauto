package de.openteilauto.openteilauto.api

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.io.*
import java.util.*
import kotlin.experimental.and
import kotlin.math.log

private const val BASE_URL =
    "https://sal2.teilauto.net/api/"

interface TeilautoApiService {
    @FormUrlEncoded
    @POST("login")
    suspend fun login(@FieldMap(encoded = true) loginRequestBody: Map<String, String>): LoginResponse

    @FormUrlEncoded
    @POST("listMyBookings")
    suspend fun getBookings(@FieldMap(encoded = true) bookingsRequestBody: Map<String, String>): BookingsResponse

    @FormUrlEncoded
    @POST("unlockVehicle")
    suspend fun unlockVehicle(@FieldMap(encoded = true) unlockRequestBody: Map<String, String>): UnlockResponse
}

class TeilautoApi {

    companion object {
        @Volatile private var INSTANCE: TeilautoApiService? = null

        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildTeilautoService(context).also { INSTANCE = it }
            }

        private fun buildTeilautoService(context: Context): TeilautoApiService {
            val cookieJar = PersistentCookieJar(context)

            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val okHttpClient = OkHttpClient.Builder()
                .cookieJar(cookieJar)
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
            return retrofit.create(TeilautoApiService::class.java)
        }
    }
}