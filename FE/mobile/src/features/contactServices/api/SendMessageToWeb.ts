import { WebViewMessageEvent } from 'react-native-webview';
import ContactService from './GetContacts';

export const handleWebViewMessage = async (
  event: WebViewMessageEvent,
  postMessage: (message: string) => void
) => {
  try {
    const data = JSON.parse(event.nativeEvent.data);
    if (data.type === 'OPEN_CONTACTS') {
      try {
        const contacts = await ContactService.getContacts();
        console.log(contacts);
        postMessage(
          JSON.stringify({
            type: 'CONTACTS_RESPONSE',
            success: true,
            contacts,
          })
        );
      } catch (error) {
        if (error instanceof Error) {
          postMessage(
            JSON.stringify({
              type: 'CONTACTS_ERROR',
              success: false,
              message: error.message,
            })
          );
        }
      }
    }
  } catch (e) {
    console.error('메시지 파싱 오류', e);
  }
};
