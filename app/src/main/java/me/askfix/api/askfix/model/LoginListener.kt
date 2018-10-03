package me.askfix.api.askfix.model

import retrofit2.Call

interface LoginListener {
    fun onLoginResponse(loginResponse: LoginResponse?)
    fun onError(call: Call<LoginResponse>, t: Throwable)
}