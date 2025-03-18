'use client';

import Link from 'next/link';
import React, { useState } from 'react';
import { CustomOverlayMap, MapMarker } from 'react-kakao-maps-sdk';
import { Coordinates, Marker } from '../model/marker';

interface PlacesProps {
  markers: Marker[];
  changeCenter: (position: Coordinates) => void;
}

export default function Places({ markers, changeCenter }: PlacesProps) {
  const [info, setInfo] = useState<Marker | null>(null);

  return (
    <>
      {markers.length > 0 && (
        <div className="absolute bottom-0 flex flex-col w-full h-80 p-2 z-10 rounded-lg bg-white overflow-y-auto">
          {markers.map((marker) => (
            <div
              className="border-[0.6px] border-black"
              onClick={() => {
                setInfo(marker);
                changeCenter(marker.position);
              }}
              key={marker.id}
            >
              <h2 className="font-semibold text-lg w-48 truncate">{marker.place_name}</h2>
              <p className="text-sm self-end">{marker.address_name}</p>
              <Link target="_blank" className="text-base" href={marker.place_url}>
                상세 페이지
              </Link>
            </div>
          ))}
        </div>
      )}
      {info && (
        <CustomOverlayMap key={info.id} position={info.position}>
          <MapMarker
            key={info.id}
            onClick={() => changeCenter(info.position)}
            position={info.position}
            image={{
              src: `/house.svg`,
              size: {
                width: 32,
                height: 40,
              },
              options: {
                offset: {
                  x: 16,
                  y: 40,
                }, // 마커이미지의 옵션입니다. 마커의 좌표와 일치시킬 이미지 안에서의 좌표를 설정합니다.
                alt: `음식점`,
              },
            }}
          ></MapMarker>
        </CustomOverlayMap>
      )}
    </>
  );
}
