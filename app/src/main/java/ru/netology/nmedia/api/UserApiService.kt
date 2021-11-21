package ru.netology.nmedia.api


import retrofit2.Response
import retrofit2.http.*

interface UsersApiService {
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("user/authentication")
    @FormUrlEncoded
    suspend fun getToken (
        @Field("login") login:String,
        @Field("pass") pass: String): Response<UserIdToken>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("user/registration")
    @FormUrlEncoded
    suspend fun registration (
        @Field("login") login:String,
        @Field("pass") pass: String,
        @Field("name") name: String): Response<UserIdToken>

}
