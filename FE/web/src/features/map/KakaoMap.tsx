'use client';

import { Map, useKakaoLoader } from 'react-kakao-maps-sdk';

export const KakaoMap: React.FC = () => {
  useKakaoLoader({
    appkey: process.env.NEXT_PUBLIC_KAKAO_MAP_KEY!,
    libraries: ['services', 'clusterer', 'drawing'],
  });

  return (
    <div className="relative w-full h-full">
      <Map // 지도를 표시할 Container
        center={{ lat: 33.450701, lng: 126.570667 }}
        className="w-full h-full"
        level={3} // 지도의 확대 레벨
      />
    </div>
  );
};
