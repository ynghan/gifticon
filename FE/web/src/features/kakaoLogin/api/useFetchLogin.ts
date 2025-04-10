import { getCookieValue } from '@/shared/api/axiosInstance';
import axios from 'axios';
  const accessToken = getCookieValue('accessToken')

const accessToken = getCookieValue('accessToken');
export async function fetchKakaoLogin(code: string) {
  const response = await axios.post(
    `${process.env.NEXT_PUBLIC_BASE_URL}/api/auth/kakao/callback`,
    {
      code,
    },
    {
      headers: {
        'xx-auth': `Bearer ${accessToken}`,
      },
      withCredentials: true,
    }
  );
  if (!response.data.accessToken) {
    console.error('토큰이 응답에 없습니다.');
    throw new Error('토큰이 없습니다.');
  }
  return response.data;
}
