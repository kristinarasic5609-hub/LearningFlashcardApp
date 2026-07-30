package com.flashlearn.app.repository;

import com.flashlearn.app.model.entity.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatisticsRepository extends JpaRepository<Statistics, String> {

    Optional<Statistics> findByUserId(String userId);
}
