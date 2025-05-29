import tkinter as tk
from order_data import order_data, menu_items
from payment_screen import payment_logic

def create_table_screen(table_number):
    window = tk.Toplevel()
    window.title(f"Table {table_number}")
    window.geometry("1100x800")
    window.configure(bg="#f2f2f2")

    total_price_int = 0

    # 주문 내역 영역 (좌측)
    frame1 = tk.Frame(window, bg="#ffffff", bd=2, relief="groove")
    frame1.place(relx=0.02, rely=0.02, relwidth=0.58, relheight=0.76)

    # 메뉴 버튼 영역 (우측)
    frame2 = tk.Frame(window, bg="#f8f8f8", bd=2, relief="groove")
    frame2.place(relx=0.62, rely=0.02, relwidth=0.36, relheight=0.76)

    # 총합 금액 영역
    total_price_label = tk.Label(window, text="Total: 0원", font=("Malgun Gothic", 20, "bold"),
                                 bg="#eeeeee", anchor="w", padx=20)
    total_price_label.place(relx=0.02, rely=0.79, relwidth=0.58, relheight=0.08)

    # 하단 버튼 영역
    frame4 = tk.Frame(window, bg="#f5f5f5", bd=2, relief="ridge")
    frame4.place(relx=0.62, rely=0.79, relwidth=0.36, relheight=0.18)

    # 주문내역 출력
    def update_order_display():
        nonlocal total_price_int
        for widget in frame1.winfo_children():
            widget.destroy()

        total_price = 0
        for item, (quantity, price) in order_data.get(table_number, {}).items():
            if quantity > 0:
                total_price += price * quantity
                row = tk.Frame(frame1, bg="#ffffff")
                row.pack(fill="x", pady=4, padx=8)
                label = tk.Label(row, text=f"{item} - {quantity}개 - {price * quantity:,}원",
                                 font=("Malgun Gothic", 14), anchor="w", bg="#ffffff")
                label.pack(side="left", fill="x", expand=True)
                del_btn = tk.Button(row, text="－", bg="#ffdddd", fg="black",
                                    font=("Arial", 12), width=3,
                                    command=lambda item=item: decrease_quantity(item))
                del_btn.pack(side="right")

        total_price_label.config(text=f"Total: {total_price:,}원")
        total_price_int = total_price

    # 메뉴 클릭 시
    def add_to_order(item_name):
        if table_number not in order_data:
            order_data[table_number] = {}
        if item_name in order_data[table_number]:
            qty, price = order_data[table_number][item_name]
            order_data[table_number][item_name] = (qty + 1, price)
        else:
            order_data[table_number][item_name] = (1, menu_items[item_name])
        update_order_display()

    def decrease_quantity(item_name):
        if item_name in order_data.get(table_number, {}):
            qty, price = order_data[table_number][item_name]
            if qty > 1:
                order_data[table_number][item_name] = (qty - 1, price)
            else:
                del order_data[table_number][item_name]
            update_order_display()

    def payment_button_click():
        nonlocal total_price_int
        if total_price_int == 0:
            return
        result = payment_logic(total_price_int)
        if result:
            order_data[table_number] = {}
            window.destroy()

    # 메뉴 버튼 만들기
    for item_name, price in menu_items.items():
        btn = tk.Button(
            frame2, text=f"🍽 {item_name} - {price:,}원",
            font=("Malgun Gothic", 14, "bold"),
            bg="#ffffff", activebackground="#d4eaff",
            relief="raised", bd=2,
            height=2,
            command=lambda item_name=item_name: add_to_order(item_name)
        )
        btn.pack(fill="x", padx=20, pady=6)

    update_order_display()

    # 하단 버튼 스타일
    def styled_button(text, bg, command=None):
        return tk.Button(frame4, text=text, font=("Malgun Gothic", 16, "bold"),
                         bg=bg, fg="white", relief="raised", bd=2,
                         activebackground="#cccccc", command=command)

    styled_button("결제", "#4CAF50", payment_button_click).grid(row=0, column=0, sticky="nsew", padx=2, pady=2)
    styled_button("버튼2", "#607D8B").grid(row=0, column=1, sticky="nsew", padx=2, pady=2)
    styled_button("버튼3", "#03A9F4").grid(row=1, column=0, sticky="nsew", padx=2, pady=2)
    styled_button("닫기", "#f44336", window.destroy).grid(row=1, column=1, sticky="nsew", padx=2, pady=2)

    for i in range(2):
        frame4.grid_columnconfigure(i, weight=1)
        frame4.grid_rowconfigure(i, weight=1)
