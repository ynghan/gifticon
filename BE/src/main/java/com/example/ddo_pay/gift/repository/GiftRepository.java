package com.example.ddo_pay.gift.repository;

import com.example.ddo_pay.gift.entity.Gift;
import com.example.ddo_pay.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GiftRepository extends JpaRepository<Gift, Integer> {
    List<Gift> findByUser(User user);
    Optional<Gift> findById(Long giftId);
    Optional<Gift> findByPhoneNum(String phoneNum);

    List<Gift> findAllByPhoneNum(String phoneNum); // 여러 개가 조회될 수 있다면
}
