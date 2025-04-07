'use client';

import React, { useEffect, useCallback } from 'react';
import { Coordinates } from '../model/marker';
import { ChevronsDown, ChevronsUp } from 'lucide-react';
import { TGift } from '@/entity/gift/model/gift';
import { useFetchGift } from '@/entity/gift/api/useFetchGift';
import { CustomOverlayMap, MapMarker } from 'react-kakao-maps-sdk';
import { FadeUpContainer } from '@/widgets/fadeUpContainer';
import { Carousel, CarouselContent, CarouselItem } from '@/components/ui/carousel';
import { Card, CardContent } from '@/components/ui/card';
import { useCarouselWithMarker } from '@/shared/hooks/useCarouselWithMarker';
import { useMapStore } from '@/store/useMapStore';

interface PlacesProps {
  changeCenter: (position: Coordinates) => void;
}

export default function MyGifts({ changeCenter }: PlacesProps) {
  const { gifts } = useFetchGift();
  const { map } = useMapStore();

  const {
    selectedItem: info,
    isVisible,
    setIsVisible,
    api,
    setApi,
    handleMarkerClick: handleMarkerFromHook,
    setSelectedItem,
  } = useCarouselWithMarker<TGift>({
    items: gifts,
    changeCenter,
  });

  // 커스텀 마커 클릭 핸들러
  const handleMarker = useCallback(
    (gift: TGift) => {
      // 먼저 선택된 아이템과 중심 좌표를 설정
      setSelectedItem(gift);

      // 지도 중심 이동
      changeCenter(gift.position);

      // 리스트를 표시
      setIsVisible(true);

      // 약간의 지연 후 해당 아이템으로 Carousel 스크롤
      if (api) {
        const index = gifts.findIndex((item) => item.id === gift.id);
        if (index !== -1) {
          // setTimeout을 사용하여 상태 업데이트 후 스크롤하도록 보장
          setTimeout(() => {
            api.scrollTo(index);
          }, 50);
        }
      }
    },
    [api, gifts, changeCenter, setIsVisible, setSelectedItem],
  );

  // 지도 드래그 시 리스트 숨기기
  useEffect(() => {
    if (!map) return;

    const dragStartListener = () => {
      setIsVisible(false);
    };

    kakao.maps.event.addListener(map, 'dragstart', dragStartListener);

    return () => {
      kakao.maps.event.removeListener(map, 'dragstart', dragStartListener);
    };
  }, [map, setIsVisible]);

  return (
    <>
      {gifts
        .filter((gift) => gift.used_status !== 'BEFORE_USE')
        .map((gift) => (
          <CustomOverlayMap key={gift.id + 'marker'} position={gift.position}>
            <MapMarker
              onClick={() => handleMarker(gift)}
              position={gift.position}
              image={{
                src: `/gift.png`,
                size: {
                  width: info?.id === gift.id ? 66 : 33,
                  height: info?.id === gift.id ? 70 : 35,
                },
                options: {
                  offset: {
                    x: info?.id === gift.id ? 26 : 13,
                    y: info?.id === gift.id ? 70 : 35,
                  }, // 마커이미지의 옵션입니다. 마커의 좌표와 일치시킬 이미지 안에서의 좌표를 설정합니다.
                  alt: `음식점`,
                },
              }}
            ></MapMarker>
          </CustomOverlayMap>
        ))}
      {isVisible ? (
        <FadeUpContainer
          className={`absolute bottom-0 flex flex-col w-full h-60 p-2 z-10 rounded-lg bg-white overflow-y-auto will-change-transform`}
        >
          <div className='flex justify-center'>
            <button className='cursor-pointer' onClick={() => setIsVisible(false)}>
              <ChevronsDown />
            </button>
          </div>
          <Carousel className='w-full h-full flex items-center pl-2' setApi={setApi}>
            <CarouselContent>
              {gifts.length > 0 ? (
                gifts.map((gift) => (
                  <CarouselItem key={`${gift.id}${gift.id}`} className='basis-[90%] p-4'>
                    <Card
                      className='max-h-40'
                      onClick={() => {
                        handleMarker(gift);
                      }}
                    >
                      <CardContent className='p-4 pb-3 flex items-start'>
                        <div className='flex-1 pr-4'>
                          <h2 className='text-xl font-semibold mb-1'>
                            선물준이: {gift.send_user_name}
                          </h2>
                          <div className='flex flex-col gap-1 mb-1.5'>
                            <p className='text-gray-600 text-sm'>선물제목: {gift.title}</p>
                            <p className='text-gray-600 text-sm'>
                              유효기간: {gift.expiration_date}
                            </p>
                          </div>
                        </div>
                        <div className='w-20 h-20 relative flex-shrink-0'>
                          <img
                            src={gift.image || '/restaurant.png'}
                            alt={gift.title}
                            // fill
                            className='object-cover rounded-md'
                          />
                        </div>
                      </CardContent>
                    </Card>
                  </CarouselItem>
                ))
              ) : (
                <CarouselItem className='basis-full'>
                  <div className='bg-white rounded-lg p-6 text-center shadow-[0_2px_8px_rgba(0,0,0,0.15)]'>
                    <p className='text-gray-500'>표시할 선물이 없습니다.</p>
                  </div>
                </CarouselItem>
              )}
            </CarouselContent>
          </Carousel>
        </FadeUpContainer>
      ) : (
        <div className='absolute bottom-0 left-1/2 -translate-x-1/2 z-10 '>
          <button className='cursor-pointer' onClick={() => setIsVisible(true)}>
            <ChevronsUp />
          </button>
        </div>
      )}
    </>
  );
}
