package com.example.geartrackapi.dao.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "quote_production_activities")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class QuoteProductionActivity extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", referencedColumnName = "id", nullable = false)
    private Quote quote;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "work_time_minutes", nullable = false)
    private Integer workTimeMinutes;
    
    @Column(name = "price", nullable = false, precision = 10, scale = 4)
    private BigDecimal price;
    
    @Column(name = "margin_percent", nullable = false, precision = 13, scale = 4)
    private BigDecimal marginPercent;
    
    @Column(name = "margin_pln", nullable = false, precision = 10, scale = 4)
    private BigDecimal marginPln;
    
    @Column(name = "ignore_min_quantity", nullable = false)
    private Boolean ignoreMinQuantity;
}