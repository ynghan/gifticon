import { useState, useEffect } from 'react';
import { CarouselApi } from '@/components/ui/carousel';
import { Coordinates } from '@/features/map/model/marker';

interface UseCarouselWithMarkerProps<T extends { id: string | number; position: Coordinates }> {
  items: T[];
  changeCenter: (position: Coordinates) => void;
  initialVisible?: boolean;
}

interface UseCarouselWithMarkerResult<T> {
  current: number;
  api: CarouselApi | undefined;
  setApi: (api: CarouselApi | undefined) => void;
  selectedItem: T | null;
  setSelectedItem: (item: T | null) => void;
  isVisible: boolean;
  setIsVisible: (value: boolean) => void;
  handleMarkerClick: (marker: T) => void;
}

export const useCarouselWithMarker = <T extends { id: string | number; position: Coordinates }>({
  items,
  changeCenter,
  initialVisible = true,
}: UseCarouselWithMarkerProps<T>): UseCarouselWithMarkerResult<T> => {
  const [selectedItem, setSelectedItem] = useState<T | null>(null);
  const [isVisible, setIsVisible] = useState(initialVisible);
  const [api, setApi] = useState<CarouselApi>();
  const [current, setCurrent] = useState(0);

  useEffect(() => {
    if (!api) return;

    const onSelect = () => {
      setCurrent(api.selectedScrollSnap());
    };

    api.on('select', onSelect);

    return () => {
      api.off('select', onSelect);
    };
  }, [api]);

  useEffect(() => {
    if (items.length > 0 && current >= 0 && current < items.length && isVisible) {
      const item = items[current];
      setSelectedItem(item);
      changeCenter(item.position);
    }
  }, [current, items, changeCenter, isVisible]);

  const handleMarkerClick = (marker: T) => {
    setSelectedItem(marker);
    changeCenter(marker.position);
    setIsVisible(true);

    if (api) {
      const index = items.findIndex((item) => item.id === marker.id);
      if (index !== -1) {
        api.scrollTo(index);
      }
    }
  };

  return {
    current,
    api,
    setApi,
    selectedItem,
    setSelectedItem,
    isVisible,
    setIsVisible,
    handleMarkerClick,
  };
};
