'use client';

import { Button } from '@/components/ui/button';
import { signIn, signOut, useSession } from 'next-auth/react';

export default function Login() {
  const { data: session } = useSession();
  console.log(session);
  const handleMessageToBE = () => {
    // TODO: BE로 메시지 전달하고 받은 response(AccessToken 등)을 상태 관리합니다.
  };
  const handleSignIn = () => {
    signIn('kakao', { callbackUrl: '/' });
  };
  return (
    <>
      <div>
        <h1>안녕하세요 또가게입니다.</h1>
        <h2>지금 카카오로 간편하게 로그인하세요!</h2>
        {session && session.user ? (
          // 로그인 되어있을 경우
          <>
            <h3>{session.user.name}님 안녕하세요!</h3>
            <Button onClick={() => signOut()}>LogOut</Button>
          </>
        ) : (
          <button onClick={handleSignIn} className='transparent cursor-pointer'>
            <img src='/kakaoLogin.svg' alt='' />
          </button>
        )}
      </div>
    </>
  );
}
