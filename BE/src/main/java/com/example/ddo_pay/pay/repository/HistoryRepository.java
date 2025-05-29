package com.example.ddo_pay.pay.repository;

import com.example.ddo_pay.pay.entity.AssetType;
import com.example.ddo_pay.pay.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {


    List<History> findByDdoPay_IdAndType(Long payId, AssetType assetType);



}
