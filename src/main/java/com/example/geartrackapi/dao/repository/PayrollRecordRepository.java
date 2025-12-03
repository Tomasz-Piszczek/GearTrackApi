package com.example.geartrackapi.dao.repository;

import com.example.geartrackapi.dao.model.PayrollRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PayrollRecordRepository extends JpaRepository<PayrollRecord, UUID> {
    List<PayrollRecord> findByYearAndMonthAndUserIdAndHiddenFalseOrderByEmployeeId(Integer year, Integer month, UUID userId);
    List<PayrollRecord> findByUserIdAndHiddenFalse(UUID userId);
    Page<PayrollRecord> findByUserIdAndHiddenFalse(UUID userId, Pageable pageable);
    List<PayrollRecord> findByEmployeeIdAndHiddenFalse(UUID employeeId);
    
}