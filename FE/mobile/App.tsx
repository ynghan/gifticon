import React from 'react';
import {WebView} from 'react-native-webview';

function App() {
  return (
    <WebView
      source={{uri: '본인 배포 url(http://192.xxx :3000'}}
      style={{flex: 1}}
    />
  );
}

export default App;
