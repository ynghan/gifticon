"use client";

import { useHandleContacts } from "../api/HandleContacts";
import { motion } from "motion/react";
import { Button } from "@/components/ui/button";
import { useState } from "react";
import SearchBar from "./SearchBar";

interface ContactListProps {
  setSelectedContact: React.Dispatch<
    React.SetStateAction<{ name: string; phoneNumber: string }>
  >;
  setIsShow: React.Dispatch<React.SetStateAction<boolean>>;
}

const ContactList = ({ setSelectedContact, setIsShow }: ContactListProps) => {
  const { contacts } = useHandleContacts();
  const sortedContacts = [...contacts].sort((a, b) => {
    const nameA = a.firstName ? a.firstName : "";
    const nameB = b.firstName ? b.firstName : "";

    // 한글이 영어보다 먼저 오도록 정렬 로직 변경
    const testerA = /[ㄱ-ㅎㅏ-ㅣ가-힣]/.test(nameA);
    const testerB = /[ㄱ-ㅎㅏ-ㅣ가-힣]/.test(nameB);

    if (testerA && !testerB) {
      return -1; // a가 한글이고 b가 영문이면 a를 먼저 정렬
    } else if (!testerA && testerB) {
      return 1; // b가 한글이고 a가 영문이면 b를 먼저 정렬
    } else {
      // 둘 다 한글이거나 둘 다 영문인 경우 localeCompare로 정렬
      return nameA.localeCompare(nameB, "ko");
    }
  });
  const [query, setQuery] = useState<string>("");
  const handleContact = (name: string, phoneNumber: string) => {
    setSelectedContact({ name, phoneNumber });
    setIsShow(false);
  };

  const filteredContacts = sortedContacts.filter((contact) => {
    const firstName = contact.firstName || ""; // firstName이 undefined일 경우 빈 문자열 할당
    return (
      typeof firstName === "string" && // firstName이 문자열인지 확인
      firstName.includes(query)
    );
  });

  return (
    <motion.div
      className="flex flex-col h-full items-center"
      initial={{ transform: "translateY(100%)" }}
      animate={{ transform: "translateY(0)" }}
    >
      <>
        <SearchBar setQuery={setQuery} setIsShow={setIsShow} />
        <ul>
          {filteredContacts.map((contact) => (
            <li key={contact.phoneNumbers[0].value} className="flex gap-4">
              <div>
                <Button
                  onClick={() =>
                    handleContact(
                      contact.firstName,
                      contact.phoneNumbers[0].value
                    )
                  }
                >
                  {contact?.firstName}
                </Button>
              </div>
            </li>
          ))}
        </ul>
      </>
    </motion.div>
  );
};

export default ContactList;
