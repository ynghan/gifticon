'use client';

import React, { useMemo, useEffect, useCallback } from 'react';
import { Coordinates, Marker } from '../model/marker';
import { ChevronsDown, ChevronsUp } from 'lucide-react';
import { useFetchFavoriteStores } from '@/entity/store/api/useFetchFavoriteStores';
import Markers from './Markers';
import { FadeUpContainer } from '@/widgets/fadeUpContainer';
import { Carousel, CarouselContent, CarouselItem } from '@/components/ui/carousel';
import { Card, CardContent } from '@/components/ui/card';
import { useCarouselWithMarker } from '@/shared/hooks/useCarouselWithMarker';
import { useMapStore } from '@/store/useMapStore';

interface PlacesProps {
  changeCenter: (position: Coordinates) => void;
}

// favoriteStore 타입 정의
interface FavoriteStore {
  address_name: string;
  id: string | number;
  place_name: string;
  position: Coordinates;
  visited_count: number;
  main_image_url?: string;
}

export default function MyStores({ changeCenter }: PlacesProps) {
  const { favoriteStores } = useFetchFavoriteStores() as { favoriteStores: FavoriteStore[] };
  const { map } = useMapStore();

  const markers = useMemo(
    () =>
      favoriteStores.map(({ address_name, id, place_name, position, visited_count }) => ({
        address_name,
        id,
        place_name,
        place_url: address_name,
        position,
        visited_count,
      })),
    [favoriteStores],
  );

  const {
    selectedItem: info,
    isVisible,
    setIsVisible,
    api,
    setApi,
    handleMarkerClick: handleMarkerFromHook,
    setSelectedItem,
  } = useCarouselWithMarker<Marker>({
    items: markers,
    changeCenter,
  });

  // 커스텀 마커 클릭 핸들러
  const handleMarker = useCallback(
    (marker: Marker) => {
      // 먼저 선택된 아이템과 중심 좌표를 설정
      setSelectedItem(marker);
      changeCenter(marker.position);

      // 리스트를 표시하고 Carousel 스크롤
      if (api) {
        const index = markers.findIndex((item) => item.id === marker.id);
        if (index !== -1) {
          api.scrollTo(index);
        }
      }
      setIsVisible(true);
    },
    [api, markers, changeCenter, setIsVisible, setSelectedItem],
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
      <Markers
        onClick={handleMarker}
        imgSrc='/myStore.png'
        markers={markers}
        selectedMarker={info}
      />
      {isVisible ? (
        <FadeUpContainer
          className={`absolute bottom-0 flex flex-col w-full h-60 p-2 z-10 rounded-lg bg-white overflow-y-auto will-change-transform`}
        >
          <div className='flex justify-center'>
            <button onClick={() => setIsVisible(false)}>
              <ChevronsDown />
            </button>
          </div>
          <Carousel draggable className='w-full h-full flex items-center pl-2' setApi={setApi}>
            <CarouselContent>
              {favoriteStores.length > 0 ? (
                favoriteStores.map((store) => (
                  <CarouselItem key={store.id} className='basis-[90%] p-4 '>
                    <Card
                      className='max-h-40'
                      onClick={() => {
                        const marker = markers.find((m) => m.id === store.id);
                        if (marker) {
                          handleMarker(marker);
                        }
                      }}
                    >
                      <CardContent className='p-4 pb-3 flex items-start'>
                        <div className='flex-1 pr-4'>
                          <h2 className='text-xl font-semibold mb-1'>{store.place_name}</h2>
                          <div className='flex items-center gap-1 mb-1.5'>
                            <span className='mx-1 text-gray-300'>•</span>
                            <span className='text-gray-600 text-sm'>{store.address_name}</span>
                          </div>
                          <div className='flex items-center gap-1 mb-1.5'>
                            <span className='mx-1 text-gray-300'>•</span>
                            <span className='text-gray-600 text-sm'>방문 횟수 : </span>
                            <span className='text-lg'>{store.visited_count}</span>
                          </div>
                        </div>

                        <div className='w-20 h-20 relative flex-shrink-0'>
                          <img
                            src={store.main_image_url || '/restaurant.png'}
                            alt={store.place_name}
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
                    <p className='text-gray-500'>저장된 맛집이 없습니다.</p>
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
