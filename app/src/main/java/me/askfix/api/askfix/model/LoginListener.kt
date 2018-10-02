package me.askfix.api.askfix.model

interface LoginListener {
    fun onLoginResponse(loginResponse: LoginResponse?)
}