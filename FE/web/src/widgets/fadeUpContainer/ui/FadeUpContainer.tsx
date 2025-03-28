'use client';

import { AnimatePresence, motion } from 'motion/react';
import { ReactNode } from 'react';

interface Props {
  children: ReactNode;
  className?: string;
}

export const FadeUpContainer = ({ children, className }: Props) => {
  return (
    <AnimatePresence>
      <motion.div
        initial={{ transform: 'translateY(99%)' }}
        animate={{ transform: 'translateY(0px)' }}
        exit={{ transform: 'translateY(100%)' }}
        transition={{ duration: 0.5 }}
        className={className}
      >
        {children}
      </motion.div>
    </AnimatePresence>
  );
};
