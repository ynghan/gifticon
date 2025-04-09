package com.example.ddo_pay.restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ddo_pay.restaurant.entity.CustomMenu;
import com.example.ddo_pay.restaurant.entity.UserRestaurant;

public interface CustomMenuRepository extends JpaRepository<CustomMenu, Long> {
	boolean existsByUserRestaurantAndCustomMenuName(UserRestaurant userRestaurant, String customMenuName);
}

