import React from 'react';
import { X, Tag } from 'lucide-react';
import Image from 'next/image';

interface ModalProps {
  isModalOpen: boolean; // 모달 열림 여부
  closeModal: () => void; // 모달 닫기 함수
  children: React.ReactNode;
}

const Modal: React.FC<ModalProps> = ({ isModalOpen, closeModal, children }) => {
  if (!isModalOpen) return null; // 모달이 열리지 않았으면 렌더링하지 않음

  return (
    <div
      className='fixed inset-0 bg-black/50 backdrop-blur-sm flex justify-center items-end z-50'
      onClick={closeModal}
    >
      {children}
    </div>
  );
};

export default Modal;
