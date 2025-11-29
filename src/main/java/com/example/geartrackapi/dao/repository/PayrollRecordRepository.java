package com.example.geartrackapi.dao.repository;

import com.example.geartrackapi.dao.model.PayrollRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PayrollRecordRepository extends JpaRepository<PayrollRecord, UUID> {
    List<PayrollRecord> findByYearAndMonthAndUserIdOrderByEmployeeId(Integer year, Integer month, UUID userId);
}