import { Gift, Map, PlusCircle, User, User2 } from 'lucide-react';

export const BottomBar: React.FC = () => {
  return (
    <div className="absolute bottom-0 flex justify-evenly items-center w-full h-[8dvh] z-50 bg-white border-t-2">
      <Map size={32} />
      <Gift size={32} />
      <PlusCircle size={32} />
      <User2 size={32} />
      <User size={32} />
    </div>
  );
};
