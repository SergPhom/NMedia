package ru.netology.nmedia.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import ru.netology.nmedia.BuildConfig



private const val BASE_URL = "${BuildConfig.BASE_URL}/api/users/" +
//        "slow/" +
        ""
private val logging = HttpLoggingInterceptor().apply {
    if (BuildConfig.DEBUG) {
        level = HttpLoggingInterceptor.Level.BODY
    }
}

private val okhttp = OkHttpClient.Builder()
//    .addInterceptor(logging)
//    .addInterceptor { chain ->
//        chain.proceed(chain.request().newBuilder()
//            .addHeader("Content-Type", "application/x-www-form-urlencoded")
//            .build())
//    }
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .client(okhttp)
    .build()

interface UsersApiService {
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("authentication")
    @FormUrlEncoded
    suspend fun getToken (
        @Field("login") login:String,
        @Field("pass") pass: String): Response<UserIdToken>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("registration")
    @FormUrlEncoded
    suspend fun registration (
        @Field("login") login:String,
        @Field("pass") pass: String,
        @Field("name") name: String): Response<UserIdToken>

}

object UsersApi {
    val retrofitService: UsersApiService by lazy {
        retrofit.create(UsersApiService::class.java)
    }
}