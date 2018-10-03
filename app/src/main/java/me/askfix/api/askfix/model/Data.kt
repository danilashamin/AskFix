package me.askfix.api.askfix.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Data {

    @Expose
    @SerializedName("message")
    private val message: Message? = null

    class Message {
        @Expose
        @SerializedName("payload")
        private val payload: Payload? = null
        @Expose
        @SerializedName("header")
        private val header: Header? = null
    }

    class Payload

    class Header {
        @Expose
        @SerializedName("timestamp")
        private val timestamp: String? = null
        @Expose
        @SerializedName("to")
        private val to: To? = null
        @Expose
        @SerializedName("from")
        private val from: From? = null
    }

    class To {
        @Expose
        @SerializedName("uuid")
        private val uuid: String? = null
    }

    class From {
        @Expose
        @SerializedName("name")
        private val name: String? = null
        @Expose
        @SerializedName("type")
        private val type: String? = null
        @Expose
        @SerializedName("uuid")
        private val uuid: String? = null
    }
}
