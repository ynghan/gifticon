import { axiosInstance } from '@/shared/api/axiosInstance';
import { useMutation } from '@tanstack/react-query';

export const useSendValidateGift = () => {
  const { mutate: sendValidateGift } = useMutation({
    mutationFn: async () => {
      const response = await axiosInstance.post('/api/gift/check', {
        id: 5,
        password: '123456',
      });

      window.ReactNativeWebView.postMessage(
        JSON.stringify({
          type: 'PAYMENT_REQUEST',
          token: response.data.content.token,
        }),
      );

      return response;
    },
  });

  return { sendValidateGift };
};
