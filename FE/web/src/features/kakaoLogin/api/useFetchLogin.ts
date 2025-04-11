import axios from 'axios';

export async function fetchKakaoLogin(code: string) {
  const response = await axios.post(
    `${process.env.NEXT_PUBLIC_BASE_URL}/api/auth/kakao/callback`,
    {
      code,
    }
  );
  if (!response.data.accessToken) {
    console.error('토큰이 응답에 없습니다.');
    throw new Error('토큰이 없습니다.');
  }
  return response.data;
}
