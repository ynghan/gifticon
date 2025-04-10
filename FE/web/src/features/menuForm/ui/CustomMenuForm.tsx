import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import React, { useRef } from 'react';

interface Props {
  addCustomMenu: (menu: string, price: number) => void;
}

export const CustomMenuForm = ({ addCustomMenu }: Props) => {
  const nameRef = useRef<HTMLInputElement>(null);
  const priceRef = useRef<HTMLInputElement>(null);

  const handleButton = () => {
    const name = nameRef.current?.value || '';
    const price = Number(priceRef.current?.value) || 0;

    if (!name || !price) {
      return;
    }

    addCustomMenu(name, price);

    // 입력 필드 초기화
    if (nameRef.current) nameRef.current.value = '';
    if (priceRef.current) priceRef.current.value = '';
  };

  return (
    <div className='border rounded-lg p-6 bg-white shadow-sm w-full'>
      <div className='flex flex-col gap-6'>
        <div className='flex flex-col gap-2'>
          <Label htmlFor='menuName' className='text-gray-700'>
            메뉴 이름
          </Label>
          <Input
            ref={nameRef}
            type='text'
            id='menuName'
            name='menuName'
            placeholder='메뉴 이름을 입력해주세요'
            className='border-gray-300 focus:border-gray-500 focus:ring-gray-500'
          />
        </div>
        <div className='flex flex-col gap-2'>
          <Label htmlFor='menuPrice' className='text-gray-700'>
            가격
          </Label>
          <Input
            ref={priceRef}
            type='number'
            id='menuPrice'
            name='menuPrice'
            placeholder='가격을 입력해주세요'
            className='border-gray-300 focus:border-gray-500 focus:ring-gray-500'
          />
        </div>
        <Button
          onClick={handleButton}
          className='w-full bg-gray-800 hover:bg-gray-700 text-white py-2 h-12'
        >
          메뉴 추가하기
        </Button>
      </div>
    </div>
  );
};
