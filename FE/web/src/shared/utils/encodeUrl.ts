export const encodeUrl = (str: string) => {
  if (typeof window === 'undefined') {
    function encode(value: string) {
      return Buffer.from(value, 'utf-8').toString('base64');
    }
    return encode(str);
  } else {
    return btoa(unescape(encodeURIComponent(str)));
  }
};
