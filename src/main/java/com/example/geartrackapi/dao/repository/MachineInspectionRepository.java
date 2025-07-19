package com.example.geartrackapi.dao.repository;

import com.example.geartrackapi.dao.model.MachineInspection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MachineInspectionRepository extends JpaRepository<MachineInspection, UUID> {
    
    Page<MachineInspection> findByUserId(UUID userId, Pageable pageable);
    
    Page<MachineInspection> findByUserIdAndMachineId(UUID userId, UUID machineId, Pageable pageable);
    
    @Query("SELECT mi FROM MachineInspection mi WHERE mi.userId = :userId AND mi.machineId = :machineId ORDER BY mi.inspectionDate DESC")
    List<MachineInspection> findByUserIdAndMachineIdOrderByInspectionDateDesc(@Param("userId") UUID userId, @Param("machineId") UUID machineId);
    
    @Query("SELECT mi FROM MachineInspection mi WHERE mi.userId = :userId AND mi.machineId = :machineId AND mi.inspectionDate >= :fromDate ORDER BY mi.inspectionDate ASC")
    Optional<MachineInspection> findNextInspectionByMachineId(@Param("userId") UUID userId, @Param("machineId") UUID machineId, @Param("fromDate") LocalDate fromDate);
    
    @Query("SELECT mi FROM MachineInspection mi WHERE mi.userId = :userId AND mi.machineId = :machineId AND mi.inspectionDate < :toDate ORDER BY mi.inspectionDate DESC")
    List<MachineInspection> findLastInspectionByMachineId(@Param("userId") UUID userId, @Param("machineId") UUID machineId, @Param("toDate") LocalDate toDate);
    
    @Query("SELECT mi FROM MachineInspection mi WHERE mi.userId = :userId AND mi.inspectionDate BETWEEN :startDate AND :endDate ORDER BY mi.inspectionDate DESC")
    Page<MachineInspection> findByUserIdAndInspectionDateBetween(@Param("userId") UUID userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);
    
    @Query("SELECT COUNT(mi) FROM MachineInspection mi WHERE mi.userId = :userId AND mi.machineId = :machineId")
    long countByUserIdAndMachineId(@Param("userId") UUID userId, @Param("machineId") UUID machineId);
}