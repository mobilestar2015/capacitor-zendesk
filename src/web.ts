import { WebPlugin } from '@capacitor/core';

import type { ZendeskPlugin, ZendeskConfig, ZendeskChat, ZendeskChatConfig, ZendeskHelpCenterConfig, ZendeskUser, ZendeskVisitorInfo, ZendeskPrimaryColor, ZendeskNotificationToken } from './definitions';

export class ZendeskWeb extends WebPlugin implements ZendeskPlugin {
  async initChat(config: ZendeskChat): Promise<boolean> {
    console.log('Config', config);
    return true;
  }
  async setIdentity(user: ZendeskUser): Promise<boolean> {
    console.log('User', user);
    return true;
  }
  async setVisitorInfo(user: ZendeskVisitorInfo): Promise<boolean> {
    console.log('User', user);
    return true;
  }
  async setChatConfiguration(config: ZendeskChatConfig): Promise<boolean> {
    console.log('Config', config);
    return true;
  }
  async setPrimaryColor(primaryColor: ZendeskPrimaryColor): Promise<void> {
    console.log('Primary Color', primaryColor);
  }
  async setNotificationToken(notificationToken: ZendeskNotificationToken): Promise<void> {
    console.log('Notification token', notificationToken);
  }
  async showHelpCenter(config: ZendeskHelpCenterConfig): Promise<void> {
    console.log('HelpCenter config', config);
  }
  async startChat(config: ZendeskChat): Promise<void> {
    console.log('Start chat', config);
  }
  async initialize(config: ZendeskConfig): Promise<boolean> {
    console.log('initialize', config);
    return true;
  }
}
