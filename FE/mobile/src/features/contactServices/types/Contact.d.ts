declare module '@s77rt/react-native-contacts' {
  export interface Contact {
    firstName: string;
    phoneNumbers: object[];
  }

  export function getAll(_arg: string[]): Contact[] | PromiseLike<Contact[]> {
    throw new Error('Function not implemented.');
  }
}
