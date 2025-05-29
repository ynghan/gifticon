import { axiosInstance } from '@/shared/api/axiosInstance';
import { useMutation } from '@tanstack/react-query';
import { useRouter } from 'next/navigation';

export const useSendRegisterPayPwd = () => {
  const router = useRouter();

  const { mutate: mutateSendRegisterPayPwd } = useMutation({
    mutationFn: (formData: FormData) => {
      return axiosInstance.post('/api/pay/password', formData.get('password'));
    },
    onSuccess: () => {
      router.push('/');
    },
  });

  return { mutateSendRegisterPayPwd };
};
