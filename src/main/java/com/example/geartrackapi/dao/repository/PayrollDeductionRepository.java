package com.example.geartrackapi.dao.repository;

import com.example.geartrackapi.dao.model.PayrollDeduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PayrollDeductionRepository extends JpaRepository<PayrollDeduction, UUID> {

    List<PayrollDeduction> findByPayrollRecordIdAndHiddenFalse(UUID payrollRecordId);

    List<PayrollDeduction> findByPayrollRecordIdAndOrganizationIdAndHiddenFalse(UUID payrollRecordId, UUID organizationId);

    @Query("SELECT DISTINCT pd.category FROM PayrollDeduction pd WHERE pd.organizationId = :organizationId AND pd.hidden = false")
    List<String> findDistinctCategoriesByOrganizationId(@Param("organizationId") UUID organizationId);

    List<PayrollDeduction> findByCategoryAndOrganizationIdAndHiddenFalse(String category, UUID organizationId);

    @Query("SELECT pd FROM PayrollDeduction pd JOIN pd.payrollRecord pr WHERE pr.employeeId = :employeeId AND pd.organizationId = :organizationId AND pd.hidden = false")
    List<PayrollDeduction> findByEmployeeIdAndOrganizationIdAndHiddenFalse(@Param("employeeId") UUID employeeId, @Param("organizationId") UUID organizationId);
}