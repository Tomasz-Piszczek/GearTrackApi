package com.example.geartrackapi.dao.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payroll_deductions")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollDeduction extends OrganizationalEntity {
    
    @Column(name = "payroll_record_id", nullable = false)
    private UUID payrollRecordId;
    
    @Column(name = "category", nullable = false)
    private String category;
    
    @Column(name = "note", nullable = false)
    private String note;
    
    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payroll_record_id", insertable = false, updatable = false)
    private PayrollRecord payrollRecord;
}