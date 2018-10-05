package me.askfix.api.askfix;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.annimon.stream.Stream;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import me.askfix.api.askfix.adapters.DataAdapter;
import me.askfix.api.askfix.adapters.OnDataClickListener;
import me.askfix.api.askfix.api.ApiService;
import me.askfix.api.askfix.centrifuge.DataReceiver;
import me.askfix.api.askfix.centrifuge.PublishService;
import me.askfix.api.askfix.dialogs.DataDialog;
import me.askfix.api.askfix.events.ConnectEvent;
import me.askfix.api.askfix.events.DataReceivedEvent;
import me.askfix.api.askfix.model.ChannelsAndApplicationsListener;
import me.askfix.api.askfix.model.ChannelsResponse;
import me.askfix.api.askfix.utils.ChannelNameExtractor;
import retrofit2.Call;

import static me.askfix.api.askfix.C.ACCESS_TOKEN;
import static me.askfix.api.askfix.C.CHANNELS;
import static me.askfix.api.askfix.C.CHANNELS_RESPONSE;
import static me.askfix.api.askfix.C.DATA;
import static me.askfix.api.askfix.C.DATA_RECEIVED_ACTION;
import static me.askfix.api.askfix.C.SHARED_PREFS;
import static me.askfix.api.askfix.C.UUID;

public class MainActivity extends AppCompatActivity implements OnDataClickListener {
    TextView tvConnectStatus;
    TextView tvNewMessage;
    RecyclerView rvData;

    private DataReceiver dataReceiver;
    private DataAdapter dataAdapter;
    private DataDialog dataDialog;

    public static void start(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        dataReceiver = new DataReceiver();
        dataDialog = new DataDialog(this);
        initNewMessageField();
        if (!isServiceRunning(PublishService.class)) {
            getListOfChannelsAndApplicationsAndStartService();
        } else {
            initRecyclerView();
        }
    }

    private void findViews() {
        tvConnectStatus = findViewById(R.id.tvConnectStatus);
        tvNewMessage = findViewById(R.id.tvNewMessage);
        rvData = findViewById(R.id.rvData);
    }

    private void initNewMessageField() {
        String newMessage = getIntent().getStringExtra(DATA);
        tvNewMessage.setText(newMessage != null ? String.format("New Message: %s", newMessage) : "");
        tvNewMessage.setOnClickListener(view -> {
            List<String> messageList = new ArrayList<>();
            messageList.add(newMessage);
            dataDialog.setData(ChannelNameExtractor.getChannelName(newMessage), messageList);
            dataDialog.show();
        });
    }

    private void initRecyclerView() {
        rvData.setLayoutManager(new LinearLayoutManager(this));
        dataAdapter = new DataAdapter(this, getListOfApplications(), this);
        rvData.setAdapter(dataAdapter);
    }

    @Override
    public void onDataClick(String channelName, List<String> data) {
        dataDialog.setData(channelName, data);
        dataDialog.show();
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
                writeChannelsToSharedPreferences(channelsResponse.getChannels());
                initRecyclerView();
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
        dataAdapter.updateData(event.getData());
        String channelName = ChannelNameExtractor.getChannelName(event.getData());
        Toast.makeText(this, String.format("New message in channel %s", channelName), Toast.LENGTH_SHORT).show();
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

    private void writeChannelsToSharedPreferences(List<ChannelsResponse.Channels> channels) {
        List<String> nameOfChannels = Stream.ofNullable(channels)
                .map(ChannelsResponse.Channels::getName)
                .toList();
        nameOfChannels.add("#" + getUUID());
        getSharedPrefs().edit().putStringSet(CHANNELS, new HashSet<>(nameOfChannels)).apply();
    }

    private String getUUID() {
        return getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getString(UUID, "");
    }

    private List<String> getListOfApplications() {
        return new ArrayList<>(getSharedPrefs().getStringSet(CHANNELS, new HashSet<>()));
    }

    private SharedPreferences getSharedPrefs() {
        return getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
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
