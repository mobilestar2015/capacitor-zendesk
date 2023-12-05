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
        String channelKey = call.getString("key", "");
        implementation.initialize(channelKey);
        call.resolve();
    }

    @PluginMethod
    public void setPrimaryColor(PluginCall call) {
        call.unimplemented("This function is not supported on android");
    }

    @PluginMethod
    public void startChat(PluginCall call) {
        implementation.startChat();
        call.resolve();
    }
}
