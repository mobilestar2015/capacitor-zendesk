package com.thehealthwallet.plugins.zendeskimpl;

import android.util.Log;
import zendesk.android.Zendesk;
import zendesk.messaging.android.DefaultMessagingFactory;

public class ZendeskImpl {
    private static final String TAG = "Zendesk";
    private ZendeskPlugin plugin;

    public void setPlugin(ZendeskPlugin plugin) {
        this.plugin = plugin;
    }


    public void initialize(String channelKey) {
      Zendesk.initialize(
        plugin.getContext(),
        channelKey,
        zendesk -> Log.i(TAG, "Initialization successful"),
        error -> Log.e(TAG, "Messaging failed to initialize", error),
        new DefaultMessagingFactory());
    }


    public void startChat() {
      Zendesk.getInstance().getMessaging().showMessaging(plugin.getContext());
    }
}
