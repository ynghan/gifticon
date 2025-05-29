package com.example.ddo_pay.pay.repository;

import com.example.ddo_pay.pay.entity.DdoPay;
import com.example.ddo_pay.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DdoPayRepository extends JpaRepository<DdoPay, Long> {
    boolean existsByUser(User user);
    Optional<DdoPay> findByUser(User user);

    List<DdoPay> user(User user);

    Optional<DdoPay> findByUserId(Long userId);
}
