import { WebPlugin } from '@capacitor/core';

import type { ZendeskPlugin, ZendeskConfig, ZendeskPrimaryColor } from './definitions';

export class ZendeskWeb extends WebPlugin implements ZendeskPlugin {
  async setPrimaryColor(primaryColor: ZendeskPrimaryColor): Promise<void> {
    console.log('Primary Color', primaryColor);
  }
  async startChat(): Promise<void> {
    console.log('Start chat');
  }
  async initialize(config: ZendeskConfig): Promise<void> {
    console.log('initialize', config);
  }
}
