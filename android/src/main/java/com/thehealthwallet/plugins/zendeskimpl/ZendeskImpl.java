package com.thehealthwallet.plugins.zendeskimpl;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import zendesk.answerbot.AnswerBot;
import zendesk.chat.Chat;
import zendesk.chat.ChatConfiguration;
import zendesk.chat.ChatEngine;
import zendesk.chat.ChatSessionStatus;
import zendesk.chat.ChatState;
import zendesk.chat.ObservationScope;
import zendesk.chat.Observer;
import zendesk.chat.PreChatFormFieldStatus;
import zendesk.chat.ProfileProvider;
import zendesk.chat.PushNotificationsProvider;
import zendesk.chat.VisitorInfo;
import zendesk.classic.messaging.MessagingActivity;
import zendesk.classic.messaging.MessagingConfiguration;
import zendesk.core.AnonymousIdentity;
import zendesk.core.Identity;
import zendesk.core.JwtIdentity;
import zendesk.core.Zendesk;
import zendesk.support.Guide;
import zendesk.support.Support;
import zendesk.support.guide.HelpCenterActivity;
import zendesk.support.guide.ViewArticleActivity;

public class ZendeskImpl {
    private static final String TAG = "Zendesk";

    private ArrayList<String> currentUserTags = new ArrayList<>();

    private JSONObject pendingVisitorInfo = null;
    private ObservationScope observationScope = null;
    private ZendeskPlugin plugin;

    public void setPlugin(ZendeskPlugin plugin) {
        this.plugin = plugin;
    }

    public static ArrayList<String> getArrayListOfStrings(JSONObject options, String key, String functionHint) {
        ArrayList<String> result = new ArrayList<>();

        if (!options.has(key)) {
            return result;
        }
        JSONArray arr = options.optJSONArray(key);
        if (arr == null) {
            Log.e(ZendeskImpl.TAG, "wrong type for key '" + key + "' when processing " + functionHint
              + ", expected an Array of Strings.");
            return result;
        }
        for (int i = 0; i < arr.length(); i++) {
            if (arr.isNull(i)) {
                continue;
            }
            result.add(arr.optString(i));
        }
        return result;
    }

    public static String getStringOrNull(JSONObject options, String key, String functionHint) {
        if (!options.has(key)) {
            Log.d(ZendeskImpl.TAG, functionHint + ": missing " + key);
            return null;
        }
        return options.optString(key);
    }

    public static int getIntOrDefault(JSONObject options, String key, String functionHint, int defaultValue) {
        if (!options.has(key)) {
            Log.d(ZendeskImpl.TAG, functionHint + ": missing " + key);
            return defaultValue;
        }
        return options.optInt(key);
    }

    public static boolean getBooleanOrDefault(JSONObject options, String key, String functionHint,
                                              boolean defaultValue) {
        if (!options.has(key)) {
            Log.d(ZendeskImpl.TAG, functionHint + ": missing " + key);
            return defaultValue;
        }
        return options.optBoolean(key);
    }

    public static PreChatFormFieldStatus getFieldStatusOrDefault(JSONObject options, String key,
                                                                 PreChatFormFieldStatus defaultValue) {
        if (!options.has(key)) {
            return defaultValue;
        }
        switch (options.optString(key)) {
            case "required":
                return PreChatFormFieldStatus.REQUIRED;
            case "optional":
                return PreChatFormFieldStatus.OPTIONAL;
            case "hidden":
                return PreChatFormFieldStatus.HIDDEN;
            default:
                Log.e(ZendeskImpl.TAG, "wrong type for key '" + key
                  + "' when processing startChat(preChatFormOptions), expected one of ('required' | 'optional' | 'hidden').");
                return defaultValue;
        }
    }

    public void setupChatStartObserverToSetVisitorInfo(){
        // Create a temporary observation scope until the chat is started.
        observationScope = new ObservationScope();
        Chat.INSTANCE.providers().chatProvider().observeChatState(observationScope, new Observer<ChatState>() {
            @Override
            public void update(ChatState chatState) {
                ChatSessionStatus chatStatus = chatState.getChatSessionStatus();
                // Status achieved after the PreChatForm is completed
                if (chatStatus == ChatSessionStatus.STARTED) {
                    observationScope.cancel(); // Once the chat is started disable the observation
                    observationScope = null; // Clean things up to avoid confusion.
                    if (pendingVisitorInfo == null) { return; }

                    // Update the information MID chat here. All info but Department can be updated
                    // Add here the code to set the selected visitor info *after* the preChatForm is complete
                    setVisitorInfo(pendingVisitorInfo);
                    pendingVisitorInfo = null;

                    Log.d(TAG, "Set the VisitorInfo after chat start");
                } else {
                    // There are few other statuses that you can observe but they are unused in this example
                    Log.d(TAG, "[observerSetup] - ChatSessionUpdate -> (unused) status : " + chatStatus.toString());
                }
            }
        });
    }

    private void selectVisitorInfoFieldIfPreChatFormHidden(String key, JSONObject output, JSONObject input, JSONObject shouldInclude) {
        if (!input.has(key) || (shouldInclude.has(key) && !"hidden".equals(shouldInclude.optString(key))) ) {
            return;
        }

        String value = input.optString(key);
        if (((this.plugin.getContext().getApplicationInfo().flags
          & ApplicationInfo.FLAG_DEBUGGABLE) == 0)) {
            // We don't want other Apps to monitor our app's production logs for this debug information.
            // If you patch the app to enable it yourself, that's your choice!
            value = "<stripped>";
        }

        Log.d(TAG, "selectVisitorInfo to set later " + key + " '" + value + "'");
        try {
            output.putOpt(key, input.optString(key));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ChatConfiguration.Builder loadBehaviorFlags(ChatConfiguration.Builder b, JSONObject options) {
        boolean defaultValue = true;
        String logHint = "startChat(behaviorFlags)";

        return b.withPreChatFormEnabled(getBooleanOrDefault(options, "isPreChatFormEnabled", logHint, defaultValue))
          .withTranscriptEnabled(getBooleanOrDefault(options, "isChatTranscriptPromptEnabled", logHint, defaultValue))
          .withOfflineFormEnabled(getBooleanOrDefault(options, "isOfflineFormEnabled", logHint, defaultValue))
          .withAgentAvailabilityEnabled(
            getBooleanOrDefault(options, "isAgentAvailabilityEnabled", logHint, defaultValue));
    }

    private ChatConfiguration.Builder loadPreChatFormConfiguration(ChatConfiguration.Builder b, JSONObject options) {
        PreChatFormFieldStatus defaultValue = PreChatFormFieldStatus.OPTIONAL;
        return b.withNameFieldStatus(getFieldStatusOrDefault(options, "name", defaultValue))
          .withEmailFieldStatus(getFieldStatusOrDefault(options, "email", defaultValue))
          .withPhoneFieldStatus(getFieldStatusOrDefault(options, "phone", defaultValue))
          .withDepartmentFieldStatus(getFieldStatusOrDefault(options, "department", defaultValue));
    }

    private JSONObject hiddenVisitorInfoData(JSONObject allVisitorInfo, JSONObject preChatFormOptions) {
        JSONObject output = new JSONObject();
        selectVisitorInfoFieldIfPreChatFormHidden("email", output, allVisitorInfo, preChatFormOptions);
        selectVisitorInfoFieldIfPreChatFormHidden("name", output, allVisitorInfo, preChatFormOptions);
        selectVisitorInfoFieldIfPreChatFormHidden("phone", output, allVisitorInfo, preChatFormOptions);
        return output;
    }

    private void loadTags(JSONObject options) {
        // ZendeskChat Android treats the tags persistently, so you have to add/remove
        // as things change -- aka doing a diff :-(
        // ZendeskChat iOS just lets you override the full array so this isn't
        // necessary on that side.
        if (Chat.INSTANCE.providers() == null) {
          Log.e(TAG,
            "Zendesk Internals are undefined -- did you forget to call RNZendeskModule.init(<account_key>)?");
          return;
        }

        ProfileProvider profileProvider = Chat.INSTANCE.providers().profileProvider();
        ArrayList<String> activeTags = (ArrayList<String>) currentUserTags.clone();

        ArrayList<String> allProvidedTags = ZendeskImpl.getArrayListOfStrings(options, "tags", "startChat");
        ArrayList<String> newlyIntroducedTags = (ArrayList<String>) allProvidedTags.clone();

        newlyIntroducedTags.remove(activeTags); // Now just includes tags to add
        currentUserTags.removeAll(allProvidedTags); // Now just includes tags to delete

        if (!currentUserTags.isEmpty()) {
          profileProvider.removeVisitorTags(currentUserTags, null);
        }
        if (!newlyIntroducedTags.isEmpty()) {
          profileProvider.addVisitorTags(newlyIntroducedTags, null);
        }

        currentUserTags = allProvidedTags;
    }

    private MessagingConfiguration.Builder loadBotSettings(JSONObject options,
                                                           MessagingConfiguration.Builder builder) {
        if (options == null) {
          return builder;
        }
        String botName = getStringOrNull(options, "botName", "loadBotSettings");
        if (botName != null) {
          builder = builder.withBotLabelString(botName);
        }
        int avatarDrawable = getIntOrDefault(options, "botAvatarDrawableId", "loadBotSettings", -1);
        if (avatarDrawable != -1) {
          builder = builder.withBotAvatarDrawable(avatarDrawable);
        }

        return builder;
    }

    public void initialize(String appId, String clientId, String zendeskUrl, String accountKey) {
        Zendesk.INSTANCE.init(this.plugin.getContext(), zendeskUrl, appId, clientId);
        Support.INSTANCE.init(Zendesk.INSTANCE);
        Guide.INSTANCE.init(Zendesk.INSTANCE);
        AnswerBot.INSTANCE.init(Zendesk.INSTANCE, Guide.INSTANCE);
        Chat.INSTANCE.init(this.plugin.getContext(), accountKey);
    }

    public void initChat(String accountKey) {
        Chat.INSTANCE.init(this.plugin.getContext(), accountKey);
    }

    public void setChatConfiguration(JSONObject options) {
        setVisitorInfo(options);
        loadTags(options);
    }

    public boolean setVisitorInfo(JSONObject options) {
        boolean anyValuesWereSet = false;
        VisitorInfo.Builder builder = VisitorInfo.builder();

        String name = getStringOrNull(options, "name", "visitorInfo");
        if (name != null) {
            builder = builder.withName(name);
            anyValuesWereSet = true;
        }
        String email = getStringOrNull(options, "email", "visitorInfo");
        if (email != null) {
            builder = builder.withEmail(email);
            anyValuesWereSet = true;
        }
        String phone = getStringOrNull(options, "phone", "visitorInfo");
        if (phone != null) {
            builder = builder.withPhoneNumber(phone);
            anyValuesWereSet = true;
        }

        VisitorInfo visitorInfo = builder.build();

        if (Chat.INSTANCE.providers() == null) {
            Log.e(TAG,
              "Zendesk Internals are undefined -- did you forget to call Zendesk.init(<account_key>)?");
            return false;
        }

        Chat.INSTANCE.providers().profileProvider().setVisitorInfo(visitorInfo, null);

        return anyValuesWereSet;
    }

    public void setUserIdentity(JSONObject options) {
        if (options.has("token")) {
            Identity identity = new JwtIdentity(options.optString("token"));
            Zendesk.INSTANCE.setIdentity(identity);
        } else {
            String name = options.optString("name");
            String email = options.optString("email");
            Identity identity = new AnonymousIdentity.Builder()
              .withNameIdentifier(name).withEmailIdentifier(email).build();
            Zendesk.INSTANCE.setIdentity(identity);
        }
    }

    public void setNotificationToken(String token) {
        PushNotificationsProvider pushProvider = Chat.INSTANCE.providers().pushNotificationsProvider();
        if (pushProvider != null) {
            pushProvider.registerPushToken(token);
        }
    }

    public void showHelpCenter(JSONObject options) {
        String botName = options.optString("botName", "Chat Bot");
        Activity activity = this.plugin.getActivity();
        if (options.has("withChat")) {
            HelpCenterActivity.builder()
              .withEngines(ChatEngine.engine())
              .show(activity);
        } else if (options.has("disableTicketCreation")) {
            HelpCenterActivity.builder()
              .withContactUsButtonVisible(false)
              .withShowConversationsMenuButton(false)
              .show(activity, ViewArticleActivity.builder()
                .withContactUsButtonVisible(false)
                .config());
        } else {
            HelpCenterActivity.builder()
              .show(activity);
        }
    }

    public boolean startChat(JSONObject options) {
        if (Chat.INSTANCE.providers() == null) {
            Log.e(TAG,
              "Zendesk Internals are undefined -- did you forget to call Zendesk.init(<account_key>)?");
            return false;
        }
        pendingVisitorInfo = null;
        boolean didSetVisitorInfo = setVisitorInfo(options);

        boolean showPreChatForm = getBooleanOrDefault(options, "isPreChatFormEnabled", "startChat(behaviorFlags)", true);
        boolean needsToSetVisitorInfoAfterChatStart = showPreChatForm && didSetVisitorInfo;

        ChatConfiguration.Builder chatBuilder = loadBehaviorFlags(ChatConfiguration.builder(), options);
        if (showPreChatForm) {
            JSONObject preChatFormOptions = options.optJSONObject("preChatFormOptions");
            chatBuilder = loadPreChatFormConfiguration(chatBuilder, preChatFormOptions);
            pendingVisitorInfo = hiddenVisitorInfoData(options, preChatFormOptions);
        }
        ChatConfiguration chatConfig = chatBuilder.build();

        String department = ZendeskImpl.getStringOrNull(options, "department", "startChat");
        if (department != null) {
            Chat.INSTANCE.providers().chatProvider().setDepartment(department, null);
        }

        loadTags(options);

        MessagingConfiguration.Builder messagingBuilder = loadBotSettings(
          options.optJSONObject("messagingOptions"), MessagingActivity.builder());

        if (needsToSetVisitorInfoAfterChatStart) {
            setupChatStartObserverToSetVisitorInfo();
        }

        Activity activity = this.plugin.getActivity();
        if (activity != null) {
            messagingBuilder.withEngines(ChatEngine.engine()).show(activity, chatConfig);
        } else {
            Log.e(TAG, "Could not load getCurrentActivity -- no UI can be displayed without it.");
        }

        return true;
    }
}
