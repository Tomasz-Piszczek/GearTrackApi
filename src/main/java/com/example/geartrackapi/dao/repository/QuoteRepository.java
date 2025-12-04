package com.example.geartrackapi.dao.repository;

import com.example.geartrackapi.dao.model.Quote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, UUID> {
    
    Optional<Quote> findByIdAndOrganizationIdAndHiddenFalse(UUID id, UUID organizationId);
    
    @Query("SELECT q FROM Quote q WHERE q.hidden = false AND q.organizationId = :organizationId AND " +
           "(:search IS NULL OR :search = '' OR " +
           "UPPER(q.documentNumber) LIKE UPPER(CONCAT('%', :search, '%')) OR " +
           "UPPER(q.contractorCode) LIKE UPPER(CONCAT('%', :search, '%')) OR " +
           "UPPER(q.contractorName) LIKE UPPER(CONCAT('%', :search, '%')) OR " +
           "UPPER(q.productCode) LIKE UPPER(CONCAT('%', :search, '%')) OR " +
           "UPPER(q.productName) LIKE UPPER(CONCAT('%', :search, '%')))")
    Page<Quote> findBySearchAndOrganization(@Param("search") String search, @Param("organizationId") UUID organizationId, Pageable pageable);
    
    @Query("SELECT COUNT(q) FROM Quote q WHERE q.hidden = false AND q.organizationId = :organizationId AND " +
           "q.createdAt >= :startOfMonth AND q.createdAt < :startOfNextMonth AND " +
           "q.documentNumber LIKE CONCAT('OFE/%', :monthYear, '%')")
    Long countQuotesForMonthAndOrganization(@Param("startOfMonth") LocalDateTime startOfMonth, 
                            @Param("startOfNextMonth") LocalDateTime startOfNextMonth,
                            @Param("monthYear") String monthYear,
                            @Param("organizationId") UUID organizationId);
    

}