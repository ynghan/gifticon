package com.example.ddo_pay.pay.repository;

import com.example.ddo_pay.pay.entity.DdoPay;
import com.example.ddo_pay.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DdoPayRepository extends JpaRepository<DdoPay, Long> {
    boolean existsByUser(User user);
}
