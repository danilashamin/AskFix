package me.askfix.api.askfix;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.annimon.stream.Stream;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import centrifuge.Centrifuge;
import centrifuge.Client;
import centrifuge.ConnectHandler;
import centrifuge.DisconnectHandler;
import centrifuge.EventHub;
import centrifuge.PublishEvent;
import centrifuge.PublishHandler;
import centrifuge.Subscription;
import centrifuge.SubscriptionEventHub;
import me.askfix.api.askfix.api.ApiService;
import me.askfix.api.askfix.centrifuge.AppConnectHandler;
import me.askfix.api.askfix.centrifuge.AppDisconnectHandler;
import me.askfix.api.askfix.centrifuge.AppPublishHandler;
import me.askfix.api.askfix.centrifuge.OnPublishListener;
import me.askfix.api.askfix.model.ChannelsAndApplicationsListener;
import me.askfix.api.askfix.model.ChannelsResponse;
import retrofit2.Call;

import static me.askfix.api.askfix.C.ACCESS_TOKEN;
import static me.askfix.api.askfix.C.CENTRIFUGO_ADDRESS;
import static me.askfix.api.askfix.C.JWT;
import static me.askfix.api.askfix.C.SHARED_PREFS;
import static me.askfix.api.askfix.C.UUID;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.tvConnectStatus)
    TextView tvConnectStatus;

    public static void start(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        tvConnectStatus = findViewById(R.id.tvConnectStatus);
        getListOfChannelsAndApplications();
    }

    private void getListOfChannelsAndApplications() {
        ApiService.INSTANCE.getChannelsAndApplications(getAccessToken(), new ChannelsAndApplicationsListener() {
            @Override
            public void onChannelsAndApplicationsResponse(@org.jetbrains.annotations.Nullable ChannelsResponse channelsResponse) {
                subscribe(channelsResponse);

            }

            @Override
            public void onError(@NotNull Call<ChannelsResponse> call, @NotNull Throwable t) {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    private void subscribe(ChannelsResponse channelsResponse) {
        EventHub events = Centrifuge.newEventHub();
        ConnectHandler connectHandler = new AppConnectHandler(this);
        DisconnectHandler disconnectHandler = new AppDisconnectHandler(this);

        events.onConnect(connectHandler);
        events.onDisconnect(disconnectHandler);


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
            tvConnectStatus.setText(e.toString());
            return;
        }
        SubscriptionEventHub subEvents = Centrifuge.newSubscriptionEventHub();
        PublishHandler publishHandler = new AppPublishHandler((sub, event) -> {
            runOnUiThread(() -> tvConnectStatus.setText(new String(event.getData())));
        });
        subEvents.onPublish(publishHandler);

        Stream.ofNullable(channelsResponse.getChannels()).forEach(channel -> {
            try {
                Subscription sub = client.subscribe(String.format("%s:%s", channel.getDomain(), channel.getUuid()), subEvents);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


    }

    private String getAccessToken() {
        return getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getString(ACCESS_TOKEN, "");
    }

    private String getJWT() {
        return getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getString(JWT, "");
    }

    private String getUUID() {
        return getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getString(UUID, "");
    }
}
