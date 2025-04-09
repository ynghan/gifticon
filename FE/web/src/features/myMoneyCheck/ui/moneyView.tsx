'use client';

import { useFetchBalance } from '../api/useFetchMyMoney';
import { useRouter } from 'next/navigation';
import { useState } from 'react';

export const MoneyView = () => {
  const router = useRouter();
  const { balance } = useFetchBalance();

  const [isModalOpen, setIsModalOpen] = useState(false);

  const closeModal = () => {
    setIsModalOpen(false);
  };

  const handleCharge = () => {
    router.push('/moneyCharge');
  };

  const handlePay = () => {
    setIsModalOpen(true);
  };

  return (
    <div className='bg-yellow-400 rounded-xl p-6 text-center'>
      <div className='text-sm text-gray-700 mb-2'>또페이 머니</div>
      <div className='text-2xl font-normal mb-4'>
        {(balance?.pay_balance ?? 0).toLocaleString()}원
      </div>
      <div className='flex gap-2 justify-center'>
        <button
          onClick={handleCharge}
          className='bg-white/90 text-gray-800 px-6 py-2 rounded-lg font-medium'
        >
          충전하기
        </button>
        <button
          onClick={handlePay}
          className='bg-white/90 text-gray-800 px-6 py-2 rounded-lg font-medium'
        >
          결제하기
        </button>
      </div>
    </div>
  );
};
