package me.askfix.api.askfix.api

import me.askfix.api.askfix.C.BASE_URL
import me.askfix.api.askfix.model.ChannelsAndApplicationsListener
import me.askfix.api.askfix.model.ChannelsResponse
import me.askfix.api.askfix.model.LoginListener
import me.askfix.api.askfix.model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiService {
    private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()!!

    private val retrofitService = retrofit.create(RetrofitService::class.java)

    fun login(username: String, password: String, loginListener: LoginListener) {
        val loginResponse = retrofitService.getLoginResponse(username, password)
        loginResponse.enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                loginListener.onError(call, t)
            }

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                loginListener.onLoginResponse(response.body())
            }

        })
    }

    fun getChannelsAndApplications(accessToken: String?, channelsAndApplicationsListener: ChannelsAndApplicationsListener) {
        val channelsResponse = retrofitService.getChannelsAndApplications(accessToken)
        channelsResponse.enqueue(object : Callback<ChannelsResponse> {
            override fun onFailure(call: Call<ChannelsResponse>, t: Throwable) {
                channelsAndApplicationsListener.onError(call, t)
            }

            override fun onResponse(call: Call<ChannelsResponse>, response: Response<ChannelsResponse>) {
                channelsAndApplicationsListener.onChannelsAndApplicationsResponse(response.body())
            }

        })
    }
}