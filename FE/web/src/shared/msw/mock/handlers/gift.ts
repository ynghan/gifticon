import { API_URL, BASE_URL } from '@/shared/constants/url';
import { http, HttpResponse } from 'msw';
import { gifticons } from '../data/gift';

export const giftHandlers = [
  http.get(`${BASE_URL}${API_URL.gift}`, () => {
    console.log('받은 기프티콘');
    return HttpResponse.json(gifticons);
  }),
];
