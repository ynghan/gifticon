import React, {useEffect} from 'react';
import {WebView} from 'react-native-webview';
import {checkCameraAndAudioPermissions} from './src/shared/utils/CheckCameraAndAudioPermissions';
import checkContactsPermission from './src/shared/utils/CheckContactsPermission ';
import checkLocationPermission from './src/shared/utils/CheckLocationPermission';
import checkNotificationPermission from './src/shared/utils/CheckNotificationPermission';
import checkStoragePermission from './src/shared/utils/CheckPhotoStoragePermission';
function App() {
  
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

  return (
    <WebView source={{uri: 'http://192.168.56.1:3000'}} style={{flex: 1}} />
  );
}

export default App;
