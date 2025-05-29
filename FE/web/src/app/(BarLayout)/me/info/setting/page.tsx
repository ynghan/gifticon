'use client';

import { Button } from '@/components/ui/button';
import { useAuthStore } from '@/store/auth';
export default function SettingPage() {
  const { logout } = useAuthStore()
  const handleLogout = () => {
    // document.cookie = `accessToken=; path=/; expires=${new Date(0).toUTCString()}`;
    logout()
    window.location.reload();
  };

  return (
    <div className='p-4'>
      <h1 className='text-lg font-semibold mb-4'>설정</h1>
      <div>
        <Button
          onClick={handleLogout}
          className='bg-white/90 text-gray-800 px-6 py-2 rounded-lg font-medium'
        >
          로그아웃
        </Button>
      </div>
    </div>
  );
}
