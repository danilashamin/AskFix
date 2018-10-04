package me.askfix.api.askfix

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import centrifuge.Centrifuge
import com.annimon.stream.Stream
import kotlinx.android.synthetic.main.activity_main.*
import me.askfix.api.askfix.C.ACCESS_TOKEN
import me.askfix.api.askfix.C.CENTRIFUGO_ADDRESS
import me.askfix.api.askfix.C.JWT
import me.askfix.api.askfix.C.SHARED_PREFS
import me.askfix.api.askfix.C.UUID
import me.askfix.api.askfix.api.ApiService
import me.askfix.api.askfix.centrifuge.AppConnectHandler
import me.askfix.api.askfix.centrifuge.AppDisconnectHandler
import me.askfix.api.askfix.centrifuge.AppPublishHandler
import me.askfix.api.askfix.model.ChannelsAndApplicationsListener
import me.askfix.api.askfix.model.ChannelsResponse
import retrofit2.Call


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
        val events = Centrifuge.newEventHub()
        val connectHandler = AppConnectHandler(this)
        val disconnectHandler = AppDisconnectHandler(this)

        events.onConnect(connectHandler)
        events.onDisconnect(disconnectHandler)
        val client = Centrifuge.new_(
                CENTRIFUGO_ADDRESS,
                events,
                Centrifuge.defaultConfig()
        )
        client.setToken(getJWT())
        try {
            client.connect()
        } catch (e: Exception) {
            e.printStackTrace()
            tvConnectStatus.text = e.toString()
            return
        }


        val subEvents = Centrifuge.newSubscriptionEventHub()
        val publishHandler = AppPublishHandler(this)
        subEvents.onPublish(publishHandler)

        try {
            Stream.ofNullable(channelsResponse?.channels)
                    .forEach {
                        client.subscribe("${it.domain}:${it.uuid}", subEvents)
                    }
        } catch (e: Exception) {
            e.printStackTrace()
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
