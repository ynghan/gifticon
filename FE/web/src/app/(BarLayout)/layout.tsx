import { BottomBar } from '@/widgets/bottomBar';

export default function BarLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <main className='h-[calc(100%-80px)] pb-20'>
      {children}
      <BottomBar />
    </main>
  );
}
