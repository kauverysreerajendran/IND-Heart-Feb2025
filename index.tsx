import React, { useEffect } from 'react';
import { Alert } from 'react-native';
import { registerRootComponent } from "expo";
import { Provider } from "react-redux";
import StackNavigation from "./app/navigations/StackNavigation";
import { store } from "./app/redux/store";
import { Text as RNText, TextProps } from "react-native";
import { initializeApp } from '@react-native-firebase/app';
import messaging from '@react-native-firebase/messaging';
import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';
import firebaseConfig from './firebaseConfig';

// Custom Text component to disable font scaling globally
const Text = (props: TextProps) => <RNText {...props} allowFontScaling={false} />;

let firebaseApp;
try {
  firebaseApp = initializeApp(firebaseConfig);
  console.log('🔥 Firebase initialized successfully');
} catch (error) {
  console.error('🚨 Firebase init error:', error);
}

const App = () => {
  useEffect(() => {
    console.warn("🔥 Firebase Initialized for IND");

    async function setupFCM() {
      try {
        const authStatus = await messaging().requestPermission();
        console.log('🔔 Notification permission status:', authStatus);

        if (authStatus === messaging.AuthorizationStatus.AUTHORIZED || 
            authStatus === messaging.AuthorizationStatus.PROVISIONAL) {
          const token = await messaging().getToken();
          console.log('📲 FCM Token:', token);
          await AsyncStorage.setItem('authToken', token);
        }
      } catch (error) {
        console.error('⚠️ Error setting up FCM:', error);
      }
    }

    setupFCM();

    // Handle Foreground Notifications
    const unsubscribeOnMessage = messaging().onMessage(async remoteMessage => {
      console.log('📩 Foreground notification received:', remoteMessage);
      Alert.alert("New Notification", remoteMessage.notification?.body || "No message");
    });

    // Handle Background Notifications
    messaging().setBackgroundMessageHandler(async remoteMessage => {
      console.log('📩 Background Notification:', remoteMessage);
    });

    // Handle when the app is opened from a notification
    const unsubscribeOnOpen = messaging().onNotificationOpenedApp(remoteMessage => {
      console.log('📲 App opened from background notification:', remoteMessage);
    });

    return () => {
      unsubscribeOnMessage();
      unsubscribeOnOpen();
    };
  }, []);

  return (
    <Provider store={store}>
      <StackNavigation />
    </Provider>
  );
};

// Register the root component of the app
registerRootComponent(App);

export default App;
