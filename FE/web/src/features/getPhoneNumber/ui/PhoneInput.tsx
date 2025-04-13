'use client';
import { useRouter } from 'next/navigation';
import { useState } from 'react';

export default function PhoneInput() {
  const router = useRouter();
  const [phoneNumber, setphoneNumber] = useState('');

  // 전화번호 포맷팅 함수
  const formatPhoneNumber = (number: string) => {
    if (number.length === 11) {
      return `${number.slice(0, 3)}-${number.slice(3, 7)}-${number.slice(7)}`;
    }
    return number;
  };

  const handleNumberClick = (num: number) => {
    if (phoneNumber.length < 11) {
      setphoneNumber((prev) => prev + num);
    }
  };

  const handleDelete = () => {
    setphoneNumber((prev) => prev.slice(0, -1));
  };

  const handleClear = () => {
    setphoneNumber('');
  };

  const handleCharge = () => {
    const isValidPhoneNumber = (number: string) => {
      const phoneRegex = /^01([0|1|6|7|8|9])(\d{7,8})$/;
      return phoneRegex.test(number);
    };

    if (phoneNumber && isValidPhoneNumber(phoneNumber)) {
      // API 통신 로직이 들어갑니다.
      router.push(`/`);
    } else {
      alert('유효한 전화번호를 입력해주세요.');
      setphoneNumber('');
    }
  };

  return (
    <div className='flex flex-col h-full bg-gray-50'>
      <header className='flex items-center p-4 border-b bg-white shadow-sm'>
        <h1 className='flex-1 text-center text-lg font-semibold text-gray-900'>
          전화번호 입력
        </h1>
      </header>
      <main className='flex flex-col justify-between flex-grow p-6'>
        <section className='mb-6'>
          <div className='border rounded-lg p-4 bg-white shadow'>
            <div className='text-sm text-gray-500 mb-2'>전화번호</div>
            {/* 포맷팅된 전화번호 표시 */}
            <div className='text-2xl font-bold text-gray-800'>
              {formatPhoneNumber(phoneNumber)}
            </div>
          </div>
        </section>
        <section className='text-center text-gray-500 mb-6'>
          <p>
            전화번호 입력이 완료되지 않으면 또가게 서비스를 이용할 수 없습니다!
          </p>
        </section>
        <section className='grid grid-cols-3 gap-2 mb-6'>
          {[1, 2, 3, 4, 5, 6, 7, 8, 9].map((num) => (
            <button
              key={num}
              onClick={() => handleNumberClick(num)}
              className='h-20 text-xl font-medium rounded-lg bg-white shadow hover:bg-gray-100'
            >
              {num}
            </button>
          ))}
          <button
            onClick={handleClear}
            className='h-20 text-xl font-medium rounded-lg bg-rose-100 shadow hover:bg-rose-200 text-gray-600'
          >
            C
          </button>
          <button
            onClick={() => handleNumberClick(0)}
            className='h-20 text-xl font-medium rounded-lg bg-white shadow hover:bg-gray-100'
          >
            0
          </button>
          <button
            onClick={handleDelete}
            className='h-20 text-xl font-medium rounded-lg bg-white shadow hover:bg-gray-100'
          >
            ←
          </button>
        </section>
        <button
          onClick={handleCharge}
          disabled={!phoneNumber}
          className={`w-full py-4 rounded-lg text-white font-semibold transition ${
            phoneNumber ? 'bg-yellow-400 hover:bg-yellow-500' : 'bg-gray-200'
          }`}
        >
          등록하기
        </button>
      </main>
    </div>
  );
}
