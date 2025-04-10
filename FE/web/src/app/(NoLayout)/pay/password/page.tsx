'use client';

import React, { useState, Suspense } from 'react';
import { X, Lock } from 'lucide-react';
import { useRouter, useSearchParams } from 'next/navigation';
import { Button } from '@/components/ui/button';
import { axiosInstance } from '@/shared/api/axiosInstance';
import { API_URL } from '@/shared/constants/url';
import { TCharge } from '@/entity/store/model/charge';
import { useFormStore } from '@/store/form';

function PayPassword() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [input, setInput] = useState<string>('');
  const from = searchParams.get('from');
  const amount = searchParams.get('amount');
  const recipient = searchParams.get('recipient');
  const storeName = searchParams.get('storeName');
  const formData = useFormStore((state) => state.formData);

  const handleKeyPress = (key: string) => {
    if (input.length < 6) {
      setInput((prevInput) => prevInput + key);
    }
  };

  const handleBackspace = () => {
    setInput((prevInput) => prevInput.slice(0, -1));
  };

  const handleClear = () => {
    setInput('');
  };

  const handleSubmit = async () => {
    if (from === 'giftForm') {
      const response = await axiosInstance.post('/api/gift', formData);
      if (response.data.status.code !== 200) return;
      router.push(
        `/pay/completed?from=giftForm&amount=${amount}&recipient=${recipient}&storeName=${storeName}`
      );
    } else if (from === 'moneyCharge') {
      if (input.length < 6) {
        return;
      }
      // 여기서 input과 amount를 넣어서 충전 API를 요청하려고 하는데
      // useFetchCharge 훅을 사용하려고 하는데 훅을 사용하면 안되고
      // 바로 요청을 보내야 한다.
      const response = await axiosInstance.post<TCharge>(`${API_URL.charge}`, {
        amount: amount,
        password: input,
      });
      console.log(response.data);
      if (response.data.status.code === 200) {
        router.push(`/pay/completed?from=moneyCharge&amount=${amount}`);
      }
    }
  };

  return (
    <div className='min-h-screen bg-gray-50'>
      {/* 헤더 */}
      <div className='fixed top-0 left-0 right-0 bg-white border-b z-10'>
        <div className='max-w-md mx-auto px-4 h-16 flex items-center justify-between'>
          <Button
            variant='ghost'
            size='icon'
            className='hover:bg-gray-100'
            onClick={() => router.back()}
          >
            <X className='h-5 w-5 text-gray-500' />
          </Button>
          <h1 className='text-lg font-semibold text-gray-900'>결제 비밀번호</h1>
          <div className='w-10' /> {/* 균형을 위한 빈 공간 */}
        </div>
      </div>

      {/* 메인 컨텐츠 */}
      <div className='pt-20 pb-8 px-4'>
        <div className='max-w-md mx-auto bg-white rounded-2xl shadow-sm p-6 space-y-8'>
          {/* 안내 메시지 */}
          <div className='text-center space-y-2'>
            <div className='flex justify-center'>
              <Lock className='h-8 w-8 text-primary' />
            </div>
            <p className='text-gray-600'>
              결제를 진행하기 위해 비밀번호를 입력해주세요
            </p>
          </div>

          {/* 비밀번호 표시 */}
          <div className='flex justify-center space-x-3'>
            {[...Array(6)].map((_, index) => (
              <div
                key={index}
                className={`w-3 h-3 rounded-full transition-all duration-200 ${
                  index < input.length ? 'bg-primary scale-125' : 'bg-gray-200'
                }`}
              />
            ))}
          </div>

          {/* 키패드 */}
          <div className='grid grid-cols-3 gap-4'>
            {[1, 2, 3, 4, 5, 6, 7, 8, 9, 'C', 0, '←'].map((num, index) => (
              <button
                key={index}
                onClick={() => {
                  if (num === '←') {
                    handleBackspace();
                  } else if (num === 'C') {
                    handleClear();
                  } else {
                    handleKeyPress(num.toString());
                  }
                }}
                className={`aspect-square text-xl font-medium rounded-xl transition-colors ${
                  num === 'C'
                    ? 'bg-red-50 text-red-500 hover:bg-red-100'
                    : 'bg-gray-50 text-gray-700 hover:bg-gray-100'
                }`}
              >
                {num}
              </button>
            ))}
          </div>

          {/* 결제 버튼 */}
          <Button
            className='w-full py-6 text-lg font-medium bg-primary hover:bg-primary/90'
            onClick={handleSubmit}
            disabled={input.length !== 6}
          >
            결제하기
          </Button>
        </div>
      </div>
    </div>
  );
}

export default function PinDemo() {
  return (
    <Suspense
      fallback={
        <div className='min-h-screen bg-gray-50 flex items-center justify-center'>
          <div className='text-center'>
            <h2 className='text-xl font-semibold text-gray-900 mb-2'>
              로딩 중...
            </h2>
            <p className='text-gray-600'>잠시만 기다려주세요.</p>
          </div>
        </div>
      }
    >
      <PayPassword />
    </Suspense>
  );
}
