package com.example.geartrackapi.dao.repository;

import com.example.geartrackapi.dao.model.PayrollDeduction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PayrollDeductionRepository extends JpaRepository<PayrollDeduction, UUID> {
    
    List<PayrollDeduction> findByPayrollRecordIdAndHiddenFalse(UUID payrollRecordId);
    
    @Query("SELECT pd FROM PayrollDeduction pd " +
           "JOIN pd.payrollRecord pr " +
           "JOIN pr.employee e " +
           "WHERE e.id = :employeeId AND pd.organizationId = :organizationId AND pd.hidden = false " +
           "ORDER BY pd.createdAt DESC")
    Page<PayrollDeduction> findByEmployeeIdAndOrganizationIdAndHiddenFalseOrderByCreatedAtDesc(@Param("employeeId") UUID employeeId, @Param("organizationId") UUID organizationId, Pageable pageable);
    
    @Query("SELECT pd FROM PayrollDeduction pd " +
           "JOIN pd.payrollRecord pr " +
           "JOIN pr.employee e " +
           "WHERE e.id = :employeeId AND LOWER(pd.category) LIKE LOWER(CONCAT('%', :category, '%')) AND pd.organizationId = :organizationId AND pd.hidden = false " +
           "ORDER BY pd.createdAt DESC")
    Page<PayrollDeduction> findByEmployeeIdAndCategoryContainingIgnoreCaseAndOrganizationIdAndHiddenFalseOrderByCreatedAtDesc(@Param("employeeId") UUID employeeId, @Param("category") String category, @Param("organizationId") UUID organizationId, Pageable pageable);
    
    @Query("SELECT DISTINCT pd.category FROM PayrollDeduction pd " +
           "WHERE pd.organizationId = :organizationId AND pd.hidden = false " +
           "ORDER BY pd.category")
    List<String> findDistinctCategoriesByOrganizationIdAndHiddenFalse(@Param("organizationId") UUID organizationId);
}