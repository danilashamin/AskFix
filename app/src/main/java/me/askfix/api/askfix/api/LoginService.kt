package me.askfix.api.askfix.api

import me.askfix.api.askfix.model.LoginResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LoginService {
    @FormUrlEncoded
    @POST("site/login")
    fun getLoginResponse(@Field("username") username: String, @Field("password") password: String): Call<LoginResponse>
}