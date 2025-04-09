import tkinter as tk
from table_screen import create_table_screen

global_wid = 1024
global_he = 768

def create_main_screen():
    window = tk.Tk()
    window.title("Main Screen")
    window.geometry(f"{global_wid}x{global_he}")
    window.configure(bg="#f0f2f5")

    # Header
    header = tk.Label(window, text="POS 테이블 선택", font=("Malgun Gothic", 24, "bold"),
                      bg="#e6f2ff", fg="#333", pady=20)
    header.pack(fill="x")

    # 가운데 프레임
    center_frame = tk.Frame(window, bg="#f0f2f5")
    center_frame.pack(expand=True)

    # 테이블 버튼 스타일
    def create_table_btn(text, table_number):
        return tk.Button(
            center_frame,
            text=text,
            font=("Malgun Gothic", 18, "bold"),
            width=15, height=8,
            bg="#ffffff", fg="#000000",
            relief="raised", bd=3,
            activebackground="#d0ebff",
            command=lambda: create_table_screen(table_number)
        )

    table1 = create_table_btn("🍽 테이블 1", 1)
    table2 = create_table_btn("🍽 테이블 2", 2)

    table1.grid(row=0, column=0, padx=40, pady=40)
    table2.grid(row=0, column=1, padx=40, pady=40)

    # 하단 버튼 프레임
    bottom_frame = tk.Frame(window, bg="#f0f2f5")
    bottom_frame.pack(pady=30)

    def bottom_btn(text, color="#ffffff"):
        return tk.Button(
            bottom_frame,
            text=text,
            font=("Malgun Gothic", 14),
            width=40, height=2,
            bg=color,
            relief="ridge", bd=2
        )

    bottom_btn("🔄 긴 버튼 1", "#e6ffe6").grid(row=0, column=0, padx=20)
    bottom_btn("⚙ 긴 버튼 2", "#fff5e6").grid(row=0, column=1, padx=20)

    window.mainloop()

if __name__ == "__main__":
    create_main_screen()
