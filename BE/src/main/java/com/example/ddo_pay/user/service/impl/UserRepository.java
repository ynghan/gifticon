package com.example.ddo_pay.user.service.impl;

import com.example.ddo_pay.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
