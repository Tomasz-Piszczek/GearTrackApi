package com.example.geartrackapi.dao.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "machine_inspections")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MachineInspection extends BaseEntity {
    
    @Column(name = "inspection_date", nullable = false)
    private LocalDate inspectionDate;
    
    @Column(name = "machine_id", nullable = false)
    private UUID machineId;
    
    @Column(name = "notes")
    private String notes;
    
    @Column(name = "status")
    private String status;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_id", insertable = false, updatable = false)
    private Machine machine;
}