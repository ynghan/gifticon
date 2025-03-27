// TypeScript 타입 정의 파일 -> Next.js 에서 window.ReactNativeWebView에 접근하기 위함

declare global {
  interface Window {
    ReactNativeWebView: {
      postMessage: (message: string) => void;
    };
  }
}

// 모듈로 인식되도록 export 문 추가
export {};