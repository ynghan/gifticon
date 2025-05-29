import { create } from 'zustand';

export const useFormStore = create((set) => ({
  formData: null, // 초기값은 null
  setFormData: (data: any) => set({ formData: data }), // formData 업데이트 함수
}));
