export async function fetchKakaoLogin(code: string) {
  try {
    const response = await fetch(
      `${process.env.NEXT_PUBLIC_API_URL}/api/users/social/kakao/login`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ code }),
      }
    );

    if (!response.ok) {
      throw new Error('로그인 실패');
    }

    const data = await response.json();
    return data;
  } catch (error) {
    console.error('로그인 처리 중 오류', error);
    throw error;
  }
}
