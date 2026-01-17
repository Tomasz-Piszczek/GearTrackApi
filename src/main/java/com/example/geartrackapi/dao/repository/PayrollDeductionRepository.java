package com.example.geartrackapi.dao.repository;

import com.example.geartrackapi.dao.model.PayrollDeduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PayrollDeductionRepository extends JpaRepository<PayrollDeduction, UUID> {
    
    List<PayrollDeduction> findByPayrollRecordIdAndHiddenFalse(UUID payrollRecordId);
    

    void deleteByPayrollRecordIdAndOrganizationId(UUID payrollRecordId, UUID organizationId);
}