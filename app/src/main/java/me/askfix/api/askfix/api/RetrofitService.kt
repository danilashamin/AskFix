package me.askfix.api.askfix.api

import me.askfix.api.askfix.model.ChannelsResponse
import me.askfix.api.askfix.model.LoginResponse
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {
    @FormUrlEncoded
    @POST("site/login")
    fun getLoginResponse(@Field("username") username: String, @Field("password") password: String): Call<LoginResponse>

    @GET("my")
    fun getChannelsAndApplications(@Query("access-token") accessToken: String?): Call<ChannelsResponse>
}