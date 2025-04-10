'use client';

import { Button } from '@/components/ui/button';
import Image from 'next/image';

export default function Login() {
  const REST_API_KEY = process.env.NEXT_PUBLIC_KAKAO_REST_API_KEY;
  const REDIRECT_URI = process.env.NEXT_PUBLIC_KAKAO_REDIRECT_URI;
  const kakaoLink = `https://kauth.kakao.com/oauth/authorize?client_id=${REST_API_KEY}&redirect_uri=${REDIRECT_URI}&response_type=code`;

  const handleKakaoSignIn = () => {
    window.location.href = kakaoLink;
  };

  // 추가 소셜 로그인 핸들러들 (실제 구현은 각각의 API에 맞게 수정 필요)
  const handleGoogleSignIn = () => {
    // Google 로그인 로직
    console.log('Google 로그인');
  };

  const handleNaverSignIn = () => {
    // Naver 로그인 로직
    console.log('Naver 로그인');
  };

  return (
    <div className='min-h-screen flex flex-col items-center justify-center bg-white'>
      <div className='w-full max-w-md px-6 flex flex-col items-center'>
        <Image
          src='/splash_image.png'
          alt='logo'
          width={150}
          height={150}
          className='mb-10'
        />
        {/* 안내 텍스트 */}
        <div className='w-full text-left mb-6'>
          <h2 className='text-xl font-normal text-black mb-1'>
            아직 회원이 아니신가요?
          </h2>
          <h3 className='text-xl font-normal text-black'>
            3초만에 로그인 하기
          </h3>
        </div>

        {/* 로그인 버튼 영역 */}
        <div className='w-full space-y-3'>
          {/* 카카오 로그인 버튼 */}
          <Button
            onClick={handleKakaoSignIn}
            className='w-full py-6 text-base font-medium bg-[#FFE500] hover:bg-[#FFE500]/90 text-black rounded-md flex items-center justify-center'
          >
            <span className='mr-2'>
              <svg
                width='28'
                height='28'
                viewBox='0 0 24 24'
                fill='none'
                xmlns='http://www.w3.org/2000/svg'
              >
                <path
                  fillRule='evenodd'
                  clipRule='evenodd'
                  d='M12 4C8.13 4 5 6.5 5 9.65C5 11.7 6.35 13.5 8.38 14.54L7.5 17.5C7.45 17.65 7.52 17.82 7.66 17.9C7.74 17.95 7.83 17.97 7.92 17.97C8.01 17.97 8.09 17.95 8.17 17.9L11.62 15.68C11.75 15.69 11.88 15.7 12 15.7C15.87 15.7 19 13.2 19 10.05C19 6.9 15.87 4 12 4Z'
                  fill='black'
                />
              </svg>
            </span>
            카카오 로그인
          </Button>
        </div>
      </div>
    </div>
  );
}
