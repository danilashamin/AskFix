package me.askfix.api.askfix

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.annimon.stream.Stream
import me.askfix.api.askfix.C.ACCESS_TOKEN
import me.askfix.api.askfix.C.JWT
import me.askfix.api.askfix.C.SHARED_PREFS
import me.askfix.api.askfix.C.UUID
import me.askfix.api.askfix.api.ApiService
import me.askfix.api.askfix.model.ChannelsAndApplicationsListener
import me.askfix.api.askfix.model.ChannelsResponse
import retrofit2.Call
import com.centrifugal.centrifuge.android.Centrifugo
import com.centrifugal.centrifuge.android.credentials.Token
import com.centrifugal.centrifuge.android.credentials.User
import com.centrifugal.centrifuge.android.listener.SubscriptionListener
import com.centrifugal.centrifuge.android.subscription.SubscriptionRequest
import me.askfix.api.askfix.C.CENTRIFUGO_ADRESS
import com.centrifugal.centrifuge.android.listener.ConnectionListener




class MainActivity : AppCompatActivity() {

    companion object {
        fun start(context: Context) {
            val starter = Intent(context, MainActivity::class.java)
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getListOfChannelsAndApplications()
    }

    private fun getListOfChannelsAndApplications() {
        ApiService.getChannelsAndApplications(getAccessToken(), object : ChannelsAndApplicationsListener {
            override fun onChannelsAndApplicationsResponse(channelsResponse: ChannelsResponse?) {
                subscribe(channelsResponse)
            }

            override fun onError(call: Call<ChannelsResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_LONG).show()
                t.printStackTrace()
            }

        })
    }

    private fun subscribe(channelsResponse: ChannelsResponse?) {
        val centrifugo = Centrifugo.Builder(CENTRIFUGO_ADRESS)
                .setUser(User(getUUID(), null))
                .setToken(Token(getJWT(), ""))
                .build()


        centrifugo.setSubscriptionListener(object : SubscriptionListener {
            override fun onSubscriptionError(channelName: String?, error: String?) {
                Log.d("subscription", "onSubscriptionError channelName = $channelName error=$error")
            }

            override fun onSubscribed(channelName: String?) {
                Log.d("subscription", "onSubscriber channelName = $channelName")

            }

            override fun onUnsubscribed(channelName: String?) {
                Log.d("subscription", "onUSubscriber channelName = $channelName")
            }

        })

        centrifugo.setConnectionListener(object : ConnectionListener {

            override fun onWebSocketOpen() {
                Log.d("subscription", "onWebSocketOpen")
            }

            override fun onConnected() {
                Log.d("subscription", "onConnected")

            }

            override fun onDisconnected(code: Int, reason: String, remote: Boolean) {
                Log.d("subscription", "onDisconnected, code = $code, reason = $reason, remote = $remote")

            }

        })

        centrifugo.connect()

        Stream.ofNullable(channelsResponse?.channels)
                .forEach {
                    centrifugo.subscribe(SubscriptionRequest("${it.domain}${it.uuid}"))
                }

    }

    private fun getAccessToken(): String {
        return getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getString(ACCESS_TOKEN, "")
                ?: ""
    }

    private fun getJWT(): String {
        return getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getString(JWT, "") ?: ""
    }

    private fun getUUID(): String {
        return getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getString(UUID, "") ?: ""
    }
}
