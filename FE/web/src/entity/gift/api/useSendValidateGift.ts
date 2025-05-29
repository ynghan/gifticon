import { axiosInstance } from '@/shared/api/axiosInstance';
import { useMutation, useQueryClient } from '@tanstack/react-query';

export type PaymentType = 'QR' | 'NFC';

export const useSendValidateGift = () => {
  const queryClient = useQueryClient();

  const { mutate: sendValidateGift, data: paymentToken } = useMutation({
    mutationFn: async ({ giftId, type }: { giftId: number; type: PaymentType }) => {
      const response = await axiosInstance.post('/api/gift/check', {
        id: giftId,
        password: '123456',
      });

      if (type === 'NFC') {
        window.ReactNativeWebView.postMessage(
          JSON.stringify({
            type: 'PAYMENT_REQUEST',
            token: response.data.content.token,
          }),
        );
      }

      return response.data.content.token;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['gifts'] });
    },
  });

  return { sendValidateGift, paymentToken };
};
