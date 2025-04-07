import React, {useEffect, useRef, useCallback} from 'react';
import {WebView, WebViewMessageEvent} from 'react-native-webview';
import {
  checkCameraAndAudioPermissions,
  checkContactsPermission,
  checkLocationPermission,
  checkNotificationPermission,
  checkStoragePermission,
} from './src/shared/utils/';
import {handleWebViewMessage} from './src/features/contactServices';
import {NativeModules} from 'react-native';

const {DdopayNFC} = NativeModules;

function App() {
  const webViewRef = useRef<WebView>(null);

  useEffect(() => {
    const authorize = async () => {
      await checkLocationPermission();
      await checkCameraAndAudioPermissions();
      await checkContactsPermission();
      await checkNotificationPermission();
      await checkStoragePermission();
    };
    authorize();
  }, []);

  const onMessage = useCallback((event: WebViewMessageEvent) => {
    const data = JSON.parse(event.nativeEvent.data);
    if (data.type === 'PAYMENT_REQUEST') {
      console.log(data.token);

      DdopayNFC.startNfcService(data.token);

      return;
    }
    handleWebViewMessage(event, message => {
      webViewRef.current?.postMessage(message);
    });
  }, []);

  return (
    <WebView
      source={{uri: 'https://j12e106.p.ssafy.io'}}
      style={{flex: 1}}
      javaScriptEnabled={true}
      onMessage={onMessage}
      ref={webViewRef}
    />
  );
}

export default App;
