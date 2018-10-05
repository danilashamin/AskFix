package me.askfix.api.askfix.centrifuge;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

import centrifuge.Client;
import centrifuge.DisconnectEvent;
import centrifuge.DisconnectHandler;
import me.askfix.api.askfix.MainActivity;
import me.askfix.api.askfix.R;

public class AppDisconnectHandler implements DisconnectHandler {
    private MainActivity context;

    public AppDisconnectHandler(Context context) {
        this.context = (MainActivity) context;
    }

    @Override
    public void onDisconnect(Client client, final DisconnectEvent event) {
        context.runOnUiThread(() -> {
            TextView tv = ((Activity) context).findViewById(R.id.tvConnectStatus);
            tv.setText("Disconnected " + event.getReason());
        });
    }
}