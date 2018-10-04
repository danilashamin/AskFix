package me.askfix.api.askfix.centrifuge

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.TextView

import centrifuge.PublishEvent
import centrifuge.PublishHandler
import centrifuge.Subscription
import me.askfix.api.askfix.MainActivity
import me.askfix.api.askfix.R

class AppPublishHandler(context: Context) : PublishHandler {
    private var context: MainActivity = context as MainActivity

    override fun onPublish(sub: Subscription, event: PublishEvent) {
        context.runOnUiThread {
            val tv = (context as Activity).findViewById<View>(R.id.tvConnectStatus) as TextView
            val data = String(event.data)
            tv.text = "${context.getString(R.string.new_publication)}${sub.channel()}: $data"
        }
    }
}