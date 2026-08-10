package com.example.geartrackapi.dao.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "payroll_records")
@Getter
@Setter
@SuperBuilder()
@NoArgsConstructor
@AllArgsConstructor
public class PayrollRecord extends OrganizationalEntity {
    
    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;
    
    @Column(name = "year", nullable = false)
    private Integer year;
    
    @Column(name = "month", nullable = false)
    private Integer month;
    
    @Column(name = "hourly_rate", precision = 10, scale = 2, nullable = false)
    private BigDecimal hourlyRate;
    
    @Column(name = "hours_worked", precision = 8, scale = 2)
    private BigDecimal hoursWorked = BigDecimal.ZERO;
    
    @Column(name = "bonus", precision = 10, scale = 2)
    private BigDecimal bonus = BigDecimal.ZERO;
    
    @Column(name = "sick_leave_pay", precision = 10, scale = 2)
    private BigDecimal sickLeavePay = BigDecimal.ZERO;
    
    @Column(name = "deductions", precision = 10, scale = 2)
    private BigDecimal deductions = BigDecimal.ZERO;
    
    @Column(name = "bank_transfer", precision = 10, scale = 2)
    private BigDecimal bankTransfer = BigDecimal.ZERO;
    
    @Column(name = "cash_amount", precision = 10, scale = 2)
    private BigDecimal cashAmount = BigDecimal.ZERO;
    
    @Column(name = "deductions_note")
    private String deductionsNote;

    @Column(name = "paid", nullable = false)
    private Boolean paid = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    private Employee employee;
    
    @OneToMany(mappedBy = "payrollRecord", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PayrollDeduction> payrollDeductions;
}