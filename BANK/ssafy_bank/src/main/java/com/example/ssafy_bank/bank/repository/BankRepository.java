package com.example.ssafy_bank.bank.repository;

import com.example.ssafy_bank.bank.entity.SsafyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankRepository extends JpaRepository<SsafyUser, Long> {
}
