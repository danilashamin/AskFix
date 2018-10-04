package me.askfix.api.askfix.centrifuge

import android.os.Handler
import android.widget.TextView
import android.content.Context
import android.app.Activity
import android.view.View
import centrifuge.Client
import centrifuge.DisconnectEvent
import centrifuge.DisconnectHandler
import me.askfix.api.askfix.MainActivity
import me.askfix.api.askfix.R

class AppDisconnectHandler(context: Context) : DisconnectHandler {
    private var context: MainActivity = context as MainActivity

    override fun onDisconnect(client: Client, event: DisconnectEvent) {
        context.runOnUiThread {
            val tv = (context as Activity).findViewById<View>(R.id.tvConnectStatus) as TextView
            tv.text = "${context.getString(R.string.disconnected)} ${event.reason}"
        }
    }
}