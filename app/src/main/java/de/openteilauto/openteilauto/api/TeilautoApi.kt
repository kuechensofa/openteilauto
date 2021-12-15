package de.openteilauto.openteilauto.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private const val BASE_URL =
    "https://sal2.teilauto.net/api/"

private val cookieJar = object : CookieJar {
    private val cookieStore = mutableMapOf<String, List<Cookie>>()

    override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
        this.cookieStore[url.host()] = cookies
    }

    override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
        val cookies = cookieStore[url.host()]
        return cookies?.toMutableList() ?: mutableListOf()
    }
}

private val okHttpClient = OkHttpClient.Builder()
    .cookieJar(cookieJar)
    .build()

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .build()

interface TeilautoApiService {
    @FormUrlEncoded
    @POST("login")
    suspend fun login(@FieldMap(encoded = true) loginRequestBody: Map<String, String>): LoginResponse

    @FormUrlEncoded
    @POST("listMyBookings")
    suspend fun getBookings(@FieldMap(encoded = true) bookingsRequestBody: Map<String, String>): BookingsResponse
}

object TeilautoApi {
    val teilautoService: TeilautoApiService by lazy {
        retrofit.create(TeilautoApiService::class.java)
    }
}