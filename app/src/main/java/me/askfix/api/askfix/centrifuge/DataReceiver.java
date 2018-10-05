package me.askfix.api.askfix.centrifuge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.greenrobot.eventbus.EventBus;

import me.askfix.api.askfix.events.ConnectEvent;
import me.askfix.api.askfix.events.DataReceivedEvent;
import me.askfix.api.askfix.events.DisconnectEvent;

import static me.askfix.api.askfix.C.CONNECT_EVENT;
import static me.askfix.api.askfix.C.DATA;
import static me.askfix.api.askfix.C.DATA_EVENT;
import static me.askfix.api.askfix.C.DATA_RECEIVED_ACTION;
import static me.askfix.api.askfix.C.DISCONNECT_EVENT;
import static me.askfix.api.askfix.C.EVENT_TYPE;

public class DataReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DATA_RECEIVED_ACTION)) {
            String eventType = intent.getStringExtra(EVENT_TYPE);
            switch (eventType) {
                case DATA_EVENT:
                    EventBus.getDefault().post(new DataReceivedEvent(intent.getStringExtra(DATA)));
                    break;
                case CONNECT_EVENT:
                    EventBus.getDefault().post(new ConnectEvent(intent.getStringExtra(DATA)));
                    break;
                case DISCONNECT_EVENT:
                    EventBus.getDefault().post(new DisconnectEvent(intent.getStringExtra(DATA)));
                    break;
            }
        }
    }
}
