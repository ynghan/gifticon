import { CustomOverlayMap, MapMarker } from 'react-kakao-maps-sdk';
import { Marker } from '../model/marker';

interface Props {
  markers: Marker[];
  onClick: (marker: Marker) => void;
  selectedMarker: Marker | null;
}

export default function Markers({ markers, onClick, selectedMarker }: Props) {
  return (
    <>
      {markers.map((marker) => (
        <CustomOverlayMap key={marker.id + 'marker'} position={marker.position}>
          <MapMarker
            onClick={() => onClick(marker)}
            position={marker.position}
            image={{
              src: `/restaurant.png`,
              size: {
                width: selectedMarker?.id === marker.id ? 44 : 22,
                height: selectedMarker?.id === marker.id ? 52 : 26,
              },
              options: {
                offset: {
                  x: selectedMarker?.id === marker.id ? 19 : 9,
                  y: selectedMarker?.id === marker.id ? 52 : 26,
                }, // 마커이미지의 옵션입니다. 마커의 좌표와 일치시킬 이미지 안에서의 좌표를 설정합니다.
                alt: `음식점`,
              },
            }}
          ></MapMarker>
        </CustomOverlayMap>
      ))}
    </>
  );
}
