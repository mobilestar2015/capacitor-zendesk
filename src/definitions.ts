export interface ZendeskConfig {
  key?: string;
  appId: string;
  clientId: string;
  url: string;
}

export interface ZendeskChat {
  department?: string;
  tags?: string;
  name?: string;
  email: string;
  phone: string;
  botName: string; // answer bot's name
  botImage: string; // bot's avatar url
  chatOnly: boolean; // true: use only chat engine, false: use with answer bot and support engine
}

export interface ZendeskUser {
  token?: string;
  name?: string;
  email?: string;
}

export interface ZendeskVisitorInfo {
  department?: string;
  tags?: string;
  name?: string;
  email: string;
  phone: string;
}

export interface ZendeskHelpCenterConfig extends ZendeskVisitorInfo {
  botName: string;
  withChat: boolean;
}

// https://developer.zendesk.com/documentation/classic-web-widget-sdks/chat-sdk-v2/ios/customizing_the_look/
export interface ZendeskChatConfig {
  chatMenuActions: boolean;
  isChatTranscriptPromptEnabled: boolean;
  isPreChatFormEnabled: boolean;
  isOfflineFormEnabled: boolean;
  isAgentAvailabilityEnabled: boolean;
}

export interface ZendeskPrimaryColor {
  primaryColor: string;
}

export interface ZendeskNotificationToken {
  notificationToken: string;
}

export interface ZendeskPlugin {
  initialize(config: ZendeskConfig): Promise<boolean>;
  initChat(config: ZendeskChat): Promise<boolean>;
  setIdentity(user: ZendeskUser): Promise<boolean>;
  setVisitorInfo(user: ZendeskVisitorInfo): Promise<boolean>;
  setChatConfiguration(config: ZendeskChatConfig): Promise<boolean>;
  setPrimaryColor(primaryColor: ZendeskPrimaryColor): Promise<void>;
  setNotificationToken(notificationToken: ZendeskNotificationToken): Promise<void>;
  showHelpCenter(config: ZendeskHelpCenterConfig): Promise<void>;
  startChat(config: ZendeskChat): Promise<void>;
}
