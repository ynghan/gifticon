import { axiosInstance } from '@/shared/api/axiosInstance';
import { useQuery } from '@tanstack/react-query';

interface MoneyResponse {
  payBalance: number;
}

export function useFetchMyMoney() {
  return useQuery<MoneyResponse>({
    queryKey: ['myMoney'],
    queryFn: async () => {
      try {
        console.log('머니 정보 요청 중...');
        const response = await axiosInstance.get<MoneyResponse>(
          '/api/pay/balance'
        );
        console.log('머니 정보 응답:', response.data);
        return response.data;
      } catch (error) {
        console.error('머니 정보 요청 실패:', error);
        throw error;
      }
    },
    retry: 1,
    retryDelay: 1000,
    staleTime: 30000, // 30초 동안 캐시 유지
  });
}
