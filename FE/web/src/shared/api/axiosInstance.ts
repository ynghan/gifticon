'use client'

import axios from 'axios';
import { BASE_URL } from '../constants/url';

export function getCookieValue(cookieName: string) {
  // 클라이언트 환경에서만 실행
  if (typeof document === 'undefined') return null;

  const cookies = document.cookie.split('; ');
  for (let cookie of cookies) {
    const [name, value] = cookie.split('=');
    if (name === cookieName) {
      return decodeURIComponent(value);
    }
  }
  return null;
}
const accessToken = getCookieValue('accessToken');
console.log(accessToken); // accessToken 출력

export const axiosInstance = axios.create({
  baseURL: BASE_URL,
  withCredentials: true,
  headers: {
    'xx-auth': `Bearer ${accessToken}`,
  },
});