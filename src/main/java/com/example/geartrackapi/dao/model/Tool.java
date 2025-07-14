package com.example.geartrackapi.dao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "tools")
@Getter
@Setter
public class Tool extends BaseEntity {
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "factory_number")
    private String factoryNumber;
    
    @Column(name = "size")
    private String size;
    
    @Column(name = "quantity")
    private Integer quantity;
    
    @Column(name = "value", precision = 10, scale = 2)
    private BigDecimal value;
    
    @OneToMany(mappedBy = "tool", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EmployeeTool> employeeTools;
}