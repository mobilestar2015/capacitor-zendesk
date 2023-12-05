export interface ZendeskConfig {
  key?: string;
}

export interface ZendeskPrimaryColor {
  primaryColor: string;
}

export interface ZendeskPlugin {
  initialize(config: ZendeskConfig): Promise<void>;
  startChat(): Promise<void>;
}
