package me.askfix.api.askfix.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ChannelsResponse {

    @Expose
    @SerializedName("applications")
    public val applications: List<Applications>? = null
    @Expose
    @SerializedName("channels")
    val channels: List<Channels>? = null

    class Applications {
        @Expose
        @SerializedName("uuid")
        val uuid: String? = null
        @Expose
        @SerializedName("description")
        val description: String? = null
        @Expose
        @SerializedName("name")
        val name: String? = null
    }

    class Channels {
        @Expose
        @SerializedName("uuid")
        val uuid: String? = null
        @Expose
        @SerializedName("description")
        val description: String? = null
        @Expose
        @SerializedName("domain")
        val domain: String? = null
        @Expose
        @SerializedName("name")
        val name: String? = null
    }
}
