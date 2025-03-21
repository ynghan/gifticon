package com.example.ddo_pay.restaurant.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class CustomMenu {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custom_menu_id")
    private Long id;
    private String name; // 메뉴명
    private int price; // 메뉴 가격
    private String image; // 메뉴 사진

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_restaurant_id")
    private UserRestaurant userRestaurant;
}
