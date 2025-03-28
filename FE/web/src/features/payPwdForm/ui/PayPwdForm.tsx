'use client';

import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import Form from 'next/form';
import React from 'react';
import { useSendRegisterPayPwd } from '../api/useSendRegisterPayPwd';

export const PayPwdForm = () => {
  const { mutateSendRegisterPayPwd } = useSendRegisterPayPwd();

  return (
    <>
      <Form action={mutateSendRegisterPayPwd} className="w-full">
        <Input name="password" type="password" className="p-1" />
        <Button className="absolute bottom-0 w-full h-20">비밀번호 설정</Button>
      </Form>
    </>
  );
};
