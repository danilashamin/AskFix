package me.askfix.api.askfix.centrifuge;

import centrifuge.PublishEvent;
import centrifuge.PublishHandler;
import centrifuge.Subscription;

public class AppPublishHandler implements PublishHandler {

    private final OnPublishListener listener;

    public AppPublishHandler(OnPublishListener listener) {
        this.listener = listener;
    }

    @Override
    public void onPublish(final Subscription sub, final PublishEvent event) {
        listener.onPublish(sub, event);
    }
}