package me.askfix.api.askfix.model

interface LoginListener {
    fun onLoginResponce(loginResponse: LoginResponse?)
}