'use client';

import { useEffect, useState } from 'react';

import { TSentGift } from '@/entity/gift/model/gift';
import { Button } from '@/components/ui/button';
import { RefreshCw, CheckCircle2 } from 'lucide-react';
import useFetchSentGift from '../api/useFetchSentGift';
import SentGiftItem from './SentGiftItem';

const SentGiftList = () => {
  const [giftNumber, setGiftNumber] = useState(0);
  const [currentList, setCurrentList] = useState<TSentGift[]>([]);
  const [activeTab, setActiveTab] = useState<'cancelable' | 'nonCancelable'>(
    'cancelable'
  );

  const sentGifts = useFetchSentGift();

  const cancelableList: TSentGift[] = [];
  const nonCancelableList: TSentGift[] = [];

  sentGifts?.forEach((gift) => {
    if (gift.used_status === 'BEFORE_USE') {
      cancelableList.push(gift);
    } else {
      nonCancelableList.push(gift);
    }
  });

  useEffect(() => {
    setGiftNumber(sentGifts?.length ?? 0);
    setCurrentList(cancelableList);
  }, [sentGifts?.length]);

  const handleListChange = (
    newList: TSentGift[],
    tab: 'cancelable' | 'nonCancelable'
  ) => {
    setCurrentList(newList);
    setActiveTab(tab);
  };

  return (
    <div className='space-y-6'>
      {/* 선물 개수 표시 */}
      <div className='text-center space-y-1'>
        <h2 className='text-xl font-normal text-gray-900'>
          친구들에게 보낸 선물이
        </h2>
        <p className='text-3xl font-normal text-[#FBBC05]'>{giftNumber}개</p>
        <p className='text-gray-500'>있어요</p>
      </div>

      {/* 필터 버튼 */}
      <div className='flex justify-center gap-2'>
        <Button
          variant={activeTab === 'cancelable' ? 'default' : 'outline'}
          className='flex items-center gap-2 px-4 py-2 rounded-full transition-all'
          style={{
            backgroundColor: activeTab === 'cancelable' ? '#FBBC05' : '#FFFFFF', // 활성화된 버튼 배경색
            color: activeTab === 'cancelable' ? '#FFFFFF' : '#000000', // 텍스트 색상
            boxShadow:
              activeTab === 'cancelable'
                ? '0px 1px 3px rgba(0, 0, 0, 0.2)'
                : undefined, // 그림자 효과
          }}
          onClick={() => handleListChange(cancelableList, 'cancelable')}
        >
          <RefreshCw className='h-4 w-4' />
          취소 가능
        </Button>
        <Button
          variant={activeTab === 'nonCancelable' ? 'default' : 'outline'}
          className='flex items-center gap-2 px-4 py-2 rounded-full transition-all'
          style={{
            backgroundColor:
              activeTab === 'nonCancelable' ? '#FBBC05' : '#FFFFFF', // 활성화된 버튼 배경색
            color: activeTab === 'nonCancelable' ? '#FFFFFF' : '#000000', // 텍스트 색상
            boxShadow:
              activeTab === 'nonCancelable'
                ? '0px 1px 3px rgba(0, 0, 0, 0.2)'
                : undefined, // 그림자 효과
          }}
          onClick={() => handleListChange(nonCancelableList, 'nonCancelable')}
        >
          <CheckCircle2 className='h-4 w-4' />
          취소 불가
        </Button>
      </div>

      {/* 선물 목록 */}
      {currentList.length > 0 ? (
        <SentGiftItem list={currentList} />
      ) : (
        <div className='text-center py-8 text-gray-500'>
          {activeTab === 'cancelable' && '취소 가능한 선물이 없습니다.'}
          {activeTab === 'nonCancelable' && '취소 불가능한 선물이 없습니다.'}
        </div>
      )}
    </div>
  );
};

export default SentGiftList;
