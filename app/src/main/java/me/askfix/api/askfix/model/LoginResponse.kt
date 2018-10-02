package me.askfix.api.askfix.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoginResponse {

    @Expose
    @SerializedName("JWT")
    private val JWT: String? = null
    @Expose
    @SerializedName("uuid")
    private val uuid: String? = null
    @Expose
    @SerializedName("access-token")
    private val accessToken: String? = null

    public fun getJWT(): String? {
        return JWT
    }

    public fun getUUID(): String? {
        return uuid
    }

    public fun getAccessToken(): String? {
        return accessToken
    }
}
