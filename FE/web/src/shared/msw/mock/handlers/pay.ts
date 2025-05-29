import { API_URL, BASE_URL } from '@/shared/constants/url';
import { http, HttpResponse } from 'msw';

export const payHandlers = [
  http.post(`${BASE_URL}${API_URL.pay_password}`, (password) => {
    console.log('비번:' + password);
    return HttpResponse.json({
      status: {
        code: 200,
        message: '또페이 생성 및 비밀번호 등록이 완료되었습니다.',
      },
    });
  }),
];
