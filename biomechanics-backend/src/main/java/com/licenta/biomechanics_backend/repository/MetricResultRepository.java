package com.licenta.biomechanics_backend.repository;

import com.licenta.biomechanics_backend.model.MetricResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetricResultRepository extends JpaRepository<MetricResult, Long> {
}
