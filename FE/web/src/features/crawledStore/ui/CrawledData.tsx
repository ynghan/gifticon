import { Separator } from '@/components/ui/separator';
import { CrawledData } from '@/entity/store/api/useFetchCrawledStore';

export const CrawledStore = ({ crawledData }: { crawledData?: CrawledData }) => {
  return (
    <>
      {crawledData && (
        <>
          <div className='flex items-center w-full px-8 gap-4'>
            <div className='w-[200px] h-[200px] relative'>
              <img
                src={crawledData.main_image_url ?? '/logo.png'}
                width={200}
                height={200}
                className='w-full h-full object-cover rounded-lg'
                alt='메인 이미지'
              />
            </div>
            <div>
              <h2 className='font-semibold text-xl mb-4'>{crawledData.place_name}</h2>
              <p>주소 : {crawledData.address_name}</p>
            </div>
          </div>
          <div className='w-full px-8 mt-4'>
            <div className='border rounded-lg p-6 bg-white shadow-sm'>
              <h2 className='text-xl font-bold mb-4 text-center'>MENU</h2>
              <div className='space-y-3'>
                {crawledData.menus.length > 0 ? (
                  crawledData.menus.map((menu) => (
                    <div key={menu.menu_name} className='flex justify-between items-center py-2'>
                      <span className='text-gray-800'>{menu.menu_name}</span>
                      <span className='text-gray-600'>{menu.menu_price}</span>
                    </div>
                  ))
                ) : (
                  <div className='text-center text-gray-500'>메뉴 없음</div>
                )}
              </div>
            </div>
          </div>
        </>
      )}
    </>
  );
};
