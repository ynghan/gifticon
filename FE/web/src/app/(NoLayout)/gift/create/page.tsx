'use client';

import { Button } from '@/components/ui/button';
import { GiftForm } from '@/features/giftForm';
import { FadeUpContainer } from '@/widgets/fadeUpContainer';
import { X } from 'lucide-react';
import { useRouter } from 'next/navigation';

export default function page() {
  const router = useRouter();

  return (
    <FadeUpContainer className="flex flex-col h-full items-center">
      <Button className="absolute top-4 left-4" onClick={() => router.back()}>
        <X />
      </Button>
      <GiftForm />
    </FadeUpContainer>
  );
}
