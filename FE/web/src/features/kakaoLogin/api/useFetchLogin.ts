import { axiosInstance } from '@/shared/api/axiosInstance';

export async function fetchKakaoLogin(code: string) {
  try {
    // console.log('카카오 로그인 시도 중...', code);

    const response = await axiosInstance.post('/api/auth/kakao/callback', {
      code,
    });

    // console.log('카카오 로그인 응답:', response.data);

    // accessToken이 있는지 확인
    if (!response.data.accessToken) {
      console.error('토큰이 응답에 없습니다.');
      throw new Error('토큰이 없습니다.');
    }

    return response.data;
  } catch (error) {
    console.error('카카오 로그인 처리 중 오류:', error);
    throw error;
  }
}
