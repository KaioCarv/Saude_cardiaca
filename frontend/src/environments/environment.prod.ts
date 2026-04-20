import { Capacitor } from '@capacitor/core';

export const environment = {
  production: true,
  apiUrl: Capacitor.isNativePlatform()
    ? 'http://172.19.193.142:8080'
    : 'http://localhost:8080',
};
