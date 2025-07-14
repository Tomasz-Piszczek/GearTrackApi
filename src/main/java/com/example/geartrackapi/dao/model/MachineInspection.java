package com.example.geartrackapi.dao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "machine_inspections")
@Getter
@Setter
public class MachineInspection extends BaseEntity {
    
    @Column(name = "inspection_date", nullable = false)
    private LocalDate inspectionDate;
    
    @Column(name = "performed_by", nullable = false)
    private String performedBy;
    
    @Column(name = "machine_id", nullable = false)
    private UUID machineId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_id", insertable = false, updatable = false)
    private Machine machine;
}