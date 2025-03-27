import useDebounce from "@/shared/utils/useDebounce";
import { useRef } from "react";

type Props = Readonly<{
  setQuery: React.Dispatch<React.SetStateAction<string>>;
  setIsShow: React.Dispatch<React.SetStateAction<boolean>>;
}>;

export default function SearchForm({ setQuery, setIsShow }: Props) {
  const inputRef = useRef<HTMLInputElement>(null);

  const handleUpdate = useDebounce((e: React.ChangeEvent<HTMLInputElement>) => {
    setQuery(e.target.value);
  }, 400);

  const handleSearchButton = () => {
    if (inputRef.current) {
      const inputValue = inputRef.current.value;
      setQuery(inputValue);
    }
  };

  const handleCloseButton = () => {
    setIsShow(false);
  };
  return (
    <div
      style={{
        display: "flex",
        justifyContent: "center",
      }}
    >
      <label htmlFor="query" className="sr-only">
        검색
      </label>
      <input
        type="search"
        name="query"
        id="query"
        className="py-1 px-1.5 border-2 border-current focus:outline-none focus:ring-1 focus:ring-blue-500 focus:relative focus:z-10"
        onChange={handleUpdate}
        ref={inputRef}
      />
      <button
        type="submit"
        className="cursor-pointer bg-black text-white py-1.5 px-2 border-none focus:outline-none focus:ring-1 focus:ring-blue-500"
        onClick={handleSearchButton}
      >
        검색
      </button>
      <button
        type="submit"
        className="cursor-pointer bg-black text-white py-1.5 px-2 border-none focus:outline-none focus:ring-1 focus:ring-blue-500"
        onClick={handleCloseButton}
      >
        닫기
      </button>
    </div>
  );
}
