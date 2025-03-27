"use client";

import { useState, useEffect, useCallback } from "react";

export const handleMessageToRn = () => {
  console.log("메시지 전송 시도");
  if (typeof window !== "undefined" && window.ReactNativeWebView) {
    window.ReactNativeWebView.postMessage(
      JSON.stringify({
        type: "OPEN_CONTACTS",
      })
    );
  }
};

export const useHandleContacts = () => {
  const [contacts, setContacts] = useState<any[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(false);

  const handleMessageFromRn = useCallback((event: MessageEvent) => {
    if (event.source !== window) {
      try {
        const message = JSON.parse(event.data);
        if (message.type === "CONTACTS_RESPONSE" && message.success) {
          setContacts(message.contacts);
          setIsLoading(false);
        }
      } catch (error) {
        console.error("Error parsing message from React Native:", error);
      }
    }
  }, []);

  useEffect(() => {
    document.addEventListener("message", handleMessageFromRn as EventListener);

    return () => {
      document.removeEventListener(
        "message",
        handleMessageFromRn as EventListener
      );
    };
  }, [handleMessageFromRn]);

  return { contacts, isLoading };
};
