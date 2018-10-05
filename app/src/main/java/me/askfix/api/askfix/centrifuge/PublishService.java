package me.askfix.api.askfix.centrifuge;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.annimon.stream.Stream;

import java.util.List;
import java.util.Objects;

import centrifuge.Centrifuge;
import centrifuge.Client;
import centrifuge.EventHub;
import centrifuge.SubscriptionEventHub;
import me.askfix.api.askfix.MainActivity;
import me.askfix.api.askfix.R;
import me.askfix.api.askfix.model.ChannelsResponse;
import me.askfix.api.askfix.utils.ChannelNameExtractor;

import static me.askfix.api.askfix.C.CENTRIFUGO_ADDRESS;
import static me.askfix.api.askfix.C.CHANNELS_RESPONSE;
import static me.askfix.api.askfix.C.CHANNEL_ID;
import static me.askfix.api.askfix.C.CONNECT_EVENT;
import static me.askfix.api.askfix.C.DATA;
import static me.askfix.api.askfix.C.DATA_EVENT;
import static me.askfix.api.askfix.C.DATA_RECEIVED_ACTION;
import static me.askfix.api.askfix.C.DEFAULT_NOTIFICATION_ID;
import static me.askfix.api.askfix.C.DISCONNECT_EVENT;
import static me.askfix.api.askfix.C.EVENT_TYPE;
import static me.askfix.api.askfix.C.JWT;
import static me.askfix.api.askfix.C.MAIN_ACTIVITY_NAME;
import static me.askfix.api.askfix.C.SHARED_PREFS;
import static me.askfix.api.askfix.C.UUID;

public class PublishService extends Service {

    private NotificationManager notificationManager;

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
        ChannelsResponse channels = (ChannelsResponse) Objects.requireNonNull(intent.getExtras()).getSerializable(CHANNELS_RESPONSE);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels();
        }
        subscribe(channels);
        return super.onStartCommand(intent, flags, startId);
    }

    private void subscribe(ChannelsResponse channelsResponse) {
        EventHub events = Centrifuge.newEventHub();

        events.onConnect((p0, p1) -> {
            if (p1.getData() != null) {
                sendData(new String(p1.getData()), CONNECT_EVENT);
            } else {
                sendData("onConnected", CONNECT_EVENT);
            }
        });
        events.onDisconnect((p0, p1) -> sendData(p1.getReason(), DISCONNECT_EVENT));


        Client client = initClient(events);

        client.setToken(getJWT());
        connectClient(client);

        SubscriptionEventHub subEvents = Centrifuge.newSubscriptionEventHub();

        subEvents.onPublish((p0, p1) -> sendData(new String(p1.getData()), DATA_EVENT));

        subscribeOnAppsChannels(client, subEvents, channelsResponse);
        subscribeOnUserChannel(client, subEvents);
    }

    private void connectClient(Client client) {
        try {
            client.connect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Client initClient(EventHub events) {
        return Centrifuge.new_(
                CENTRIFUGO_ADDRESS,
                events,
                Centrifuge.defaultConfig()
        );

    }

    private void subscribeOnAppsChannels(Client client, SubscriptionEventHub subEvents, ChannelsResponse channels) {
        Stream.ofNullable(channels.getChannels()).forEach(channel -> {
            try {
                client.subscribe(String.format("%s:%s", channel.getDomain(), channel.getUuid()), subEvents);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void subscribeOnUserChannel(Client client, SubscriptionEventHub subEvents) {
        try {
            client.subscribe("#" + getUUID(), subEvents);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isMainActivityForeground() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<ActivityManager.AppTask> runningTaskInfo = manager.getAppTasks();
            ComponentName componentInfo = runningTaskInfo.get(0).getTaskInfo().topActivity;
            if (componentInfo == null) {
                return false;
            } else return componentInfo.getClassName().equals(MAIN_ACTIVITY_NAME);
        } else {
            List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
            ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
            if (componentInfo == null) {
                return false;
            } else return componentInfo.getClassName().equals(MAIN_ACTIVITY_NAME);
        }
    }

    private void sendData(String data, String eventType) {
        if (isMainActivityForeground()) {
            Intent intent = new Intent(DATA_RECEIVED_ACTION);
            intent.putExtra(DATA, data);
            intent.putExtra(EVENT_TYPE, eventType);
            sendBroadcast(intent);
        } else if (!isAppRunning()) {
            sendNotification(data);
        }
    }

    private boolean isAppRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<ActivityManager.AppTask> runningTaskInfo = manager.getAppTasks();
            ComponentName componentInfo = runningTaskInfo.get(0).getTaskInfo().topActivity;
            return componentInfo != null;

        } else {
            List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
            ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
            return componentInfo != null;
        }
    }

    private String getJWT() {
        return getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getString(JWT, "");
    }

    private String getUUID() {
        return getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getString(UUID, "");
    }

    public void sendNotification(String data) {
        String notificationInfo = String.format("New message from channel: %s", ChannelNameExtractor.getChannelName(data));

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.putExtra(DATA, data);

        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID).
                setContentIntent(contentIntent)
                .setOngoing(false)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background))
                .setTicker(notificationInfo)
                .setContentTitle(notificationInfo)
                .setContentText(notificationInfo)
                .setWhen(System.currentTimeMillis()).build();

        notificationManager.notify(DEFAULT_NOTIFICATION_ID, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels() {
        NotificationChannel adminChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
        adminChannel.setDescription(CHANNEL_ID);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }

}
