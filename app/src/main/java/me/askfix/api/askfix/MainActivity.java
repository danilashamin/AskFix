package me.askfix.api.askfix;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import me.askfix.api.askfix.api.ApiService;
import me.askfix.api.askfix.centrifuge.DataReceiver;
import me.askfix.api.askfix.centrifuge.PublishService;
import me.askfix.api.askfix.events.ConnectEvent;
import me.askfix.api.askfix.events.DataReceivedEvent;
import me.askfix.api.askfix.model.ChannelsAndApplicationsListener;
import me.askfix.api.askfix.model.ChannelsResponse;
import retrofit2.Call;

import static me.askfix.api.askfix.C.ACCESS_TOKEN;
import static me.askfix.api.askfix.C.CHANNELS_RESPONSE;
import static me.askfix.api.askfix.C.DATA_RECEIVED_ACTION;
import static me.askfix.api.askfix.C.SHARED_PREFS;

public class MainActivity extends AppCompatActivity {
    private DataReceiver dataReceiver;

    TextView tvConnectStatus;

    public static void start(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvConnectStatus = findViewById(R.id.tvConnectStatus);
        dataReceiver = new DataReceiver();
        if(!isServiceRunning(PublishService.class)){
            getListOfChannelsAndApplicationsAndStartService();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        IntentFilter intentFilter = new IntentFilter(DATA_RECEIVED_ACTION);
        registerReceiver(dataReceiver, intentFilter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(dataReceiver);
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void getListOfChannelsAndApplicationsAndStartService() {
        ApiService.INSTANCE.getChannelsAndApplications(getAccessToken(), new ChannelsAndApplicationsListener() {
            @Override
            public void onChannelsAndApplicationsResponse(@Nullable ChannelsResponse channelsResponse) {
                Intent serviceIntent = new Intent(MainActivity.this, PublishService.class);
                serviceIntent.putExtra(CHANNELS_RESPONSE, channelsResponse);
                startService(serviceIntent);

            }

            @Override
            public void onError(@NotNull Call<ChannelsResponse> call, @NotNull Throwable t) {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataReceived(DataReceivedEvent event) {
        tvConnectStatus.setText(event.getData());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnect(ConnectEvent event) {
        tvConnectStatus.setText(event.getData());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDisconnect(ConnectEvent event) {
        tvConnectStatus.setText(event.getData());
    }

    private String getAccessToken() {
        return getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getString(ACCESS_TOKEN, "");
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
