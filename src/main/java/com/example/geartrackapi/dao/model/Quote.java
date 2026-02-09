package com.example.geartrackapi.dao.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "quotes")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Quote extends OrganizationalEntity {
    
    @Column(name = "document_number", nullable = false, length = 100)
    private String documentNumber;
    
    @Column(name = "contractor_code", nullable = false, length = 50)
    private String contractorCode;
    
    @Column(name = "contractor_name", nullable = false)
    private String contractorName;
    
    @Column(name = "product_code", nullable = false, length = 50)
    private String productCode;
    
    @Column(name = "product_name", nullable = false)
    private String productName;
    
    @Column(name = "min_quantity", nullable = false)
    private Integer minQuantity;
    
    @Column(name = "total_quantity", nullable = false)
    private Integer totalQuantity;
    
    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "user_id")
    private UUID userId;

    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Where(clause = "hidden = false")
    private List<QuoteMaterial> materials = new ArrayList<>();

    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Where(clause = "hidden = false")
    private List<QuoteProductionActivity> productionActivities = new ArrayList<>();
}