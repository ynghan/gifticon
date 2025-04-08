import { axiosInstance } from '@/shared/api/axiosInstance';
import { API_URL } from '@/shared/constants/url';
import { useQuery } from '@tanstack/react-query';
import { TGift, TGiftResponse } from '../model/gift';

export const useFetchGift = () => {
  const { data: gifts = [] } = useQuery<TGift[]>({
    queryKey: ['gifts'],
    queryFn: async () => {
      const response = await axiosInstance.get<TGiftResponse>(API_URL.gift);
      return response.data.content;
    },
  });
  return { gifts };
};
