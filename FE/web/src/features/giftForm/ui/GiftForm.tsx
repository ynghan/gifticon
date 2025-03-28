"use client";

import React, { useState } from "react";
import Form from "next/form";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";
import { handleMessageToRn } from "../api/HandleContacts";
import ContactList from "./ContactList";

export const GiftForm = () => {
  const [isShow, setIsShow] = useState(false);
  const [selectedContact, setSelectedContact] = useState({
    name: "누구에게 보낼까요?",
    phoneNumber: "",
  });
  const handleContactBtn = () => {
    handleMessageToRn();
    setIsShow(true);
  };

  return (
    <section className="flex flex-col justify-between w-full h-full">
      <div className="flex justify-center h-8 mt-8">
        <h1>선물하기</h1>
      </div>
      <Form action={""} className="h-full">
        <Input type="file" />
        <div>
          <Label htmlFor="title">나만의 메뉴명 *</Label>
          <Input
            type="text"
            id="title"
            name="title"
            placeholder="메뉴명을 입력해주세요"
            required
          />
        </div>
        <Separator />
        <div>
          <p>받는 사람:</p>
          <Button onClick={handleContactBtn}>{selectedContact.name}</Button>
        </div>
        {!isShow ? (
          <>
            <div>
              <Label htmlFor="title">가게 검색 *</Label>
              <Input
                type="search"
                id="title"
                name="title"
                placeholder="가게명을 입력해주세요"
                required
              />
            </div>
            <div>
              <Label htmlFor="message">메세지 *</Label>
              <textarea
                id="message"
                name="message"
                placeholder="메세지로 마음을 전하세요!"
                required
              />
            </div>
            <div>
              <Button onClick={() => {}}>메뉴 추가하기</Button>
            </div>
            <Button type="submit" className="w-full h-20 fixed bottom-0 ">
              (돈 얼마) 결제하기
            </Button>
          </>
        ) : (
          <ContactList
            setSelectedContact={setSelectedContact}
            setIsShow={setIsShow}
          />
        )}
      </Form>
    </section>
  );
};
