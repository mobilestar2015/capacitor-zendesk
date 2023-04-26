package com.thehealthwallet.plugins.zendeskimpl;

import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "Zendesk")
public class ZendeskPlugin extends Plugin {

    private ZendeskImpl implementation = new ZendeskImpl();

    @Override
    public void load() {
      super.load();
      implementation.setPlugin(this);
    }

    @PluginMethod
    public void initialize(PluginCall call) {
        String appId = call.getString("appId", "");
        String clientId = call.getString("clientId", "");
        String zendeskUrl = call.getString("url", "");
        String accountKey = call.getString("key", "");
        implementation.initialize(appId, clientId, zendeskUrl, accountKey);

        call.resolve();
    }

    @PluginMethod
    public void initChat(PluginCall call) {
        implementation.initChat(call.getString("key", ""));
        call.resolve();
    }

    @PluginMethod
    public void setChatConfiguration(PluginCall call) {
        implementation.setChatConfiguration(call.getData());
        call.resolve();
    }

    @PluginMethod
    public void setIdentity(PluginCall call) {
        implementation.setUserIdentity(call.getData());
        call.resolve();
    }

    @PluginMethod
    public void setVisitorInfo(PluginCall call) {
        implementation.setVisitorInfo(call.getData());
        call.resolve();
    }

    @PluginMethod
    public void setPrimaryColor(PluginCall call) {
        call.unimplemented("This function is not supported on android");
    }

    @PluginMethod
    public void setNotificationToken(PluginCall call) {
        if (!call.hasOption("token")) {
            call.reject("Missing notification token");
        }
        implementation.setNotificationToken(call.getString("token"));
        call.resolve();
    }

    @PluginMethod
    public void showHelpCenter(PluginCall call) {
        implementation.showHelpCenter(call.getData());
        call.resolve();
    }

    @PluginMethod
    public void startChat(PluginCall call) {
        implementation.startChat(call.getData());
        call.resolve();
    }
}
