import { http, HttpResponse } from 'msw';

const User = [
  { id: 'elonmusk', nickname: 'Elon Musk' },
  { id: 'zerohch0', nickname: '제로초' },
];

const baseUrl = process.env.NEXT_PUBLIC_BASE_URL;
export const handlers = [
  http.post(`${baseUrl}/api/login`, () => {
    console.log('로그인');
    return HttpResponse.json(User[1], {
      headers: {
        'Set-Cookie': 'connect.sid=msw-cookie;HttpOnly;Path=/',
      },
    });
  }),
  http.post(`${baseUrl}/api/logout`, () => {
    console.log('로그아웃');
    return new HttpResponse(null, {
      headers: {
        'Set-Cookie': 'connect.sid=;HttpOnly;Path=/;Max-Age=0',
      },
    });
  }),
  http.post(`${baseUrl}/api/users`, async ({ request }) => {
    console.log('회원가입');
    // return HttpResponse.text(JSON.stringify('user_exists'), {
    //   status: 403,
    // });
    return HttpResponse.text(JSON.stringify('ok'), {
      headers: {
        'Set-Cookie': 'connect.sid=msw-cookie;HttpOnly;Path=/',
      },
    });
  }),
];
