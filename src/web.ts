import { WebPlugin } from '@capacitor/core';

import type { ZendeskPlugin, ZendeskConfig, ZendeskChat, ZendeskChatConfig, ZendeskHelpCenterConfig, ZendeskUser, ZendeskVisitorInfo, ZendeskPrimaryColor, ZendeskNotificationToken } from './definitions';

export class ZendeskWeb extends WebPlugin implements ZendeskPlugin {
  async initChat(config: ZendeskChat): Promise<void> {
    console.log('Config', config);
  }
  async setIdentity(user: ZendeskUser): Promise<void> {
    console.log('User', user);
  }
  async setVisitorInfo(user: ZendeskVisitorInfo): Promise<void> {
    console.log('User', user);
  }
  async setChatConfiguration(config: ZendeskChatConfig): Promise<void> {
    console.log('Config', config);
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
  async initialize(config: ZendeskConfig): Promise<void> {
    console.log('initialize', config);
  }
}
