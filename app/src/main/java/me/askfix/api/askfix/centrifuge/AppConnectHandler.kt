package me.askfix.api.askfix.centrifuge

import android.widget.TextView
import android.content.Context
import android.app.Activity
import android.view.View
import centrifuge.Client
import centrifuge.ConnectEvent
import centrifuge.ConnectHandler
import me.askfix.api.askfix.MainActivity
import me.askfix.api.askfix.R

class AppConnectHandler(context: Context) : ConnectHandler {
    private var context: MainActivity = context as MainActivity

    override fun onConnect(client: Client, event: ConnectEvent) {
        context.runOnUiThread {
            val tv = (context as Activity).findViewById<View>(R.id.tvConnectStatus) as TextView
            tv.text = "${context.getString(R.string.connected)} ${event.clientID}"
        }
    }
}