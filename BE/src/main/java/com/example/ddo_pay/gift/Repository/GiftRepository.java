package com.example.ddo_pay.gift.Repository;

import com.example.ddo_pay.gift.entity.Gift;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GiftRepository extends JpaRepository<Gift, Integer> {
}
