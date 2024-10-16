package com.GaoQue.repository;

import com.GaoQue.model.RicePrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RicePriceRepository extends JpaRepository<RicePrice, Long> {
}

