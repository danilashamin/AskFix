package me.askfix.api.askfix.centrifuge;

import centrifuge.PublishEvent;
import centrifuge.Subscription;

public interface OnPublishListener {
    void onPublish(final Subscription sub, final PublishEvent event);
}
