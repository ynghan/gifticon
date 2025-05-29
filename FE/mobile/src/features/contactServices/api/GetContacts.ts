import {PermissionsAndroid} from 'react-native';
import Contacts from '@s77rt/react-native-contacts';
import type {Contact} from '@s77rt/react-native-contacts';

/**
 * 권한을 확인하고 요청합니다.
 */
const _checkPermission = async (): Promise<boolean> => {
  const permission = await PermissionsAndroid.request(
    PermissionsAndroid.PERMISSIONS.READ_CONTACTS,
    {
      title: 'Contacts',
      message: 'App would like to access your contacts.',
      buttonPositive: 'Accept',
    },
  );
  return permission === 'granted';
};

/**
 * 연락처 목록을 가져옵니다.
 */
const getContacts = async (): Promise<Contact[]> => {
  const granted = await _checkPermission();
  if (granted) {
    const contacts = await Contacts.getAll(['firstName', 'phoneNumbers']);
    return contacts;
  } else {
    throw new Error('Permission to access contacts was denied');
  }
};

export default {
  getContacts,
};
