package me.askfix.api.askfix.api

import me.askfix.api.askfix.C.BASE_URL
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

    private val loginService = retrofit.create(LoginService::class.java)

    public fun login(username: String, password: String, loginListener: LoginListener) {
        val loginResponse = loginService.getLoginResponse(username, password)
        loginResponse.enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
            }

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                loginListener.onLoginResponse(response.body())
            }

        })
    }
}