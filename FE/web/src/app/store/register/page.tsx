'use client';

import { Button } from '@/components/ui/button';
import { Separator } from '@/components/ui/separator';
import { X } from 'lucide-react';
import { motion } from 'motion/react';
import { useRouter } from 'next/navigation';

const crawledData = {
  id: null,
  place_name: '하단끝집 하단점',
  main_image_url:
    'https://search.pstatic.net/common/?autoRotate=true&type=w560_sharpen&src=https%3A%2F%2Fldb-phinf.pstatic.net%2F20201023_276%2F1603424015305CdC4w_JPEG%2FyyDdjexjV__50xn4BHRoh5sw.jpg',
  address_name: '부산 사하구 낙동대로535번길 23 1층',
  position: {
    lat: 35.104013,
    lng: 128.966366,
  },
  store_info: '(매장 소개 없음)',
  menus: [
    {
      menu_name: '숯불닭다리살',
      menu_desc: '(소개 없음)',
      menu_price: '30000',
      menu_image: '(이미지 없음)',
    },
    {
      menu_name: '숯불무뼈닭발',
      menu_desc: '(소개 없음)',
      menu_price: '20000',
      menu_image: '(이미지 없음)',
    },
    {
      menu_name: '숯불닭똥집',
      menu_desc: '(소개 없음)',
      menu_price: '10000',
      menu_image: '(이미지 없음)',
    },
    {
      menu_name: '날치알마요밥',
      menu_desc: '(소개 없음)',
      menu_price: '20000',
      menu_image: '(이미지 없음)',
    },
  ],
};

export default function page() {
  const router = useRouter();

  return (
    <motion.div
      className="flex flex-col h-full items-center"
      initial={{ transform: 'translateY(100%)' }}
      animate={{ transform: 'translateY(0)' }}
    >
      <Button className="absolute top-4 left-4" onClick={() => router.back()}>
        <X />
      </Button>
      <h1 className="text-2xl font-bold">맛집 등록</h1>
      <div className="w-40 h-40 bg-gray-200"> 대표 이미지</div>
      <h2>{crawledData.place_name}</h2>
      <p>주소 : {crawledData.address_name}</p>
      <div className="flex flex-col w-full overflow-y-auto">
        <h2>나의 메뉴</h2>
        <ul>
          {crawledData.menus.map((menu) => (
            <li key={menu.menu_name}>
              <h3>{menu.menu_name}</h3>
              <p>{menu.menu_price}원</p>
            </li>
          ))}
        </ul>
        <form>
          <input type="text" placeholder="메뉴 이름" />
          <input type="text" placeholder="가격" />
        </form>
      </div>
      <Button>등록하기</Button>
    </motion.div>
  );
}
