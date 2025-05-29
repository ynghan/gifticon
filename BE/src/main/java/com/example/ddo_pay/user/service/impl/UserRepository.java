package com.example.ddo_pay.user.service.impl;

import com.example.ddo_pay.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

   Optional<User> findByPhoneNum(String phoneNum);
   // kakaoId로 유저를 찾기 위한 메소드
   Optional<User> findByKakaoId(Long kakaoId);
}
