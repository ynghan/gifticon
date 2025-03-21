'use client';

import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

const data = [
  {
    restaurantName: '행복한 밥상',
    restaurantImage: 'url', // URL
    address: '여기가 어디여',
    latitude: 1,
    longitude: 2,
    visitedCount: 3,
  },
  {
    restaurantName: '보쌈',
    restaurantImage: 'url', // URL
    address: '여기가 보쌈여',
    latitude: 1,
    longitude: 2,
    visitedCount: 3,
  },
  {
    restaurantName: '떡볶이',
    restaurantImage: 'url', // URL
    address: '여기 떡볶이여',
    latitude: 1,
    longitude: 2,
    visitedCount: 2,
  },
  {
    restaurantName: '헤헤',
    restaurantImage: 'url', // URL
    address: '헤헤',
    latitude: 1,
    longitude: 2,
    visitedCount: 100,
  },
];

export const FavoriteStores = () => {
  return (
    <div className="flex flex-col w-full h-full gap-4">
      {data.map((store) => (
        <div className="flex items-center gap-2" key={store.address}>
          <Input id={store.address} type="checkbox" className="w-6 h-6 rounded-full" />
          <Label htmlFor={store.address}>
            <div className="flex flex-col w-full">
              <div key={store.address}>
                <p>가게명: {store.restaurantName}</p>
                <p>주소: {store.address}</p>
                <p>방문 횟수: {store.visitedCount}</p>
                <div>이미지</div>
              </div>
            </div>
          </Label>
        </div>
      ))}
    </div>
  );
};
