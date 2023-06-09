#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(ZendeskPlugin, "Zendesk",
           CAP_PLUGIN_METHOD(initialize, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(initChat, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(setIdentity, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(setVisitorInfo, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(setChatConfiguration, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(setPrimaryColor, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(setNotificationToken, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(showHelpCenter, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(startChat, CAPPluginReturnPromise);
)
