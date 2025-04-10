'use client';

import { Button } from '@/components/ui/button';
import GivenGiftList from '@/features/gitfBox/ui/GivenGiftList';
import SendGiftList from '@/features/gitfBox/ui/SentGiftList';
import { Gift, Send, X } from 'lucide-react';
import { useRouter } from 'next/navigation';
import React, { useState } from 'react';

export default function GiftPage() {
  const router = useRouter();
  const [isShowSendGift, setIsShowSendGift] = useState(false);

  const handleClickGivenBtn = () => {
    setIsShowSendGift(false);
  };

  const handleClickSendBtn = () => {
    setIsShowSendGift(true);
  };

  return (
    <div className='min-h-screen bg-gray-50'>
      <div className='max-w-2xl mx-auto px-4 py-8'>
        {/* 헤더 */}
        <div className='flex items-center justify-between mb-8'>
          <Button
            variant='ghost'
            size='icon'
            className='hover:bg-gray-100 rounded-full'
            onClick={() => router.back()}
          >
            <X className='h-5 w-5' />
          </Button>
          <h1 className='text-2xl font-normal text-gray-900'>선물함</h1>
          <div className='w-10' /> {/* 균형을 위한 빈 공간 */}
        </div>

        {/* 탭 버튼 */}
        <div className='flex justify-center gap-4 mb-8'>
          <Button
            variant={!isShowSendGift ? 'default' : 'outline'}
            className='flex items-center gap-2 px-6 py-2 rounded-full transition-all text-gray-700 hover:bg-gray-50'
            style={{
              backgroundColor: !isShowSendGift ? '#FBBC05' : '#FFFFFF', // 활성화된 버튼의 색상 설정
              color: !isShowSendGift ? '#FFFFFF' : '#000000', // 텍스트 색상 설정
              boxShadow: !isShowSendGift
                ? '0px 1px 3px rgba(0, 0, 0, 0.2)'
                : undefined, // 그림자 효과
            }}
            onClick={() => setIsShowSendGift(false)} // 받은 선물함 버튼 클릭 시 상태 변경
          >
            <Gift className='h-4 w-4' />
            받은 선물함
          </Button>
          <Button
            variant={isShowSendGift ? 'default' : 'outline'}
            className='flex items-center gap-2 px-6 py-2 rounded-full transition-all text-gray-700 hover:bg-gray-50'
            style={{
              backgroundColor: isShowSendGift ? '#FBBC05' : '#FFFFFF', // 활성화된 버튼의 색상 설정
              color: isShowSendGift ? '#FFFFFF' : '#000000', // 텍스트 색상 설정
              boxShadow: isShowSendGift
                ? '0px 1px 3px rgba(0, 0, 0, 0.2)'
                : undefined, // 그림자 효과
            }}
            onClick={() => setIsShowSendGift(true)} // 내가 보낸 선물 버튼 클릭 시 상태 변경
          >
            <Send className='h-4 w-4' />
            내가 보낸 선물
          </Button>
        </div>

        {/* 선물 목록 */}
        <div className='bg-white rounded-2xl shadow-sm p-6'>
          {isShowSendGift ? <SendGiftList /> : <GivenGiftList />}
        </div>
      </div>
    </div>
  );
}
