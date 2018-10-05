package me.askfix.api.askfix.centrifuge;

import android.widget.TextView;
import android.content.Context;

import centrifuge.Client;
import centrifuge.ConnectEvent;
import centrifuge.ConnectHandler;
import me.askfix.api.askfix.MainActivity;
import me.askfix.api.askfix.R;

public class AppConnectHandler implements ConnectHandler {
    private MainActivity context;

    public AppConnectHandler(Context context) {
        this.context = (MainActivity) context;
    }

    @Override
    public void onConnect(Client client, final ConnectEvent event) {
        context.runOnUiThread(() -> {
            TextView tv = context.findViewById(R.id.tvConnectStatus);
            tv.setText("Connected with client ID " + event.getClientID());
        });
    }
}