import { KakaoMap } from '@/features/map';
import { SearchBar } from '@/features/searchBar';

export default function Home() {
  return (
    <>
      <SearchBar />
      <KakaoMap />
    </>
  );
}
