package me.askfix.api.askfix.centrifuge;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.annimon.stream.Stream;

import java.util.List;
import java.util.Objects;

import centrifuge.Centrifuge;
import centrifuge.Client;
import centrifuge.EventHub;
import centrifuge.SubscriptionEventHub;
import me.askfix.api.askfix.model.ChannelsResponse;

import static me.askfix.api.askfix.C.CENTRIFUGO_ADDRESS;
import static me.askfix.api.askfix.C.CHANNELS_RESPONSE;
import static me.askfix.api.askfix.C.CONNECT_EVENT;
import static me.askfix.api.askfix.C.DATA;
import static me.askfix.api.askfix.C.DATA_EVENT;
import static me.askfix.api.askfix.C.DATA_RECEIVED_ACTION;
import static me.askfix.api.askfix.C.DISCONNECT_EVENT;
import static me.askfix.api.askfix.C.EVENT_TYPE;
import static me.askfix.api.askfix.C.JWT;
import static me.askfix.api.askfix.C.MAIN_ACTIVITY_NAME;
import static me.askfix.api.askfix.C.SHARED_PREFS;
import static me.askfix.api.askfix.C.UUID;

public class PublishService extends Service {
    ChannelsResponse channels;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        channels = (ChannelsResponse) Objects.requireNonNull(intent.getExtras()).getSerializable(CHANNELS_RESPONSE);
        subscribe();
        return super.onStartCommand(intent, flags, startId);
    }

    private void subscribe() {
        EventHub events = Centrifuge.newEventHub();

        events.onConnect((p0, p1) -> {
            if (p1.getData() != null) {
                sendData(new String(p1.getData()), CONNECT_EVENT);
            } else {
                sendData("onConnectedData == null", CONNECT_EVENT);
            }
        });
        events.onDisconnect((p0, p1) -> sendData(p1.getReason(), DISCONNECT_EVENT));


        Client client = Centrifuge.new_(
                CENTRIFUGO_ADDRESS,
                events,
                Centrifuge.defaultConfig()
        );


        client.setToken(getJWT());
        try {
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        SubscriptionEventHub subEvents = Centrifuge.newSubscriptionEventHub();

        subEvents.onPublish((p0, p1) -> sendData(new String(p1.getData()), DATA_EVENT));

        Stream.ofNullable(channels.getChannels()).forEach(channel -> {
            try {
                client.subscribe(String.format("%s:%s", channel.getDomain(), channel.getUuid()), subEvents);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private boolean isMainActivityForeground() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<ActivityManager.AppTask> runningTaskInfo = manager.getAppTasks();
            ComponentName componentInfo = runningTaskInfo.get(0).getTaskInfo().topActivity;
            return componentInfo.getClassName().equals(MAIN_ACTIVITY_NAME);
        } else {
            List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
            ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
            return componentInfo.getClassName().equals(MAIN_ACTIVITY_NAME);
        }
    }

    private void sendData(String data, String eventType) {
        if (isMainActivityForeground()) {
            Intent intent = new Intent(DATA_RECEIVED_ACTION);
            intent.putExtra(DATA, data);
            intent.putExtra(EVENT_TYPE, eventType);
            sendBroadcast(intent);
        } else {

        }
    }

    private String getJWT() {
        return getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getString(JWT, "");
    }

    private String getUUID() {
        return getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getString(UUID, "");
    }

}
