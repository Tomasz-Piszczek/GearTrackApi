package com.example.geartrackapi.dao.repository;

import com.example.geartrackapi.dao.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
    
    List<Organization> findAllByHiddenFalse();
    
    Optional<Organization> findByIdAndHiddenFalse(UUID id);
    
    @Query("SELECT o FROM Organization o LEFT JOIN FETCH o.users WHERE o.id = :id AND o.hidden = false")
    Optional<Organization> findByIdWithUsersAndHiddenFalse(@Param("id") UUID id);
    
    Optional<Organization> findByOrganizationNameAndHiddenFalse(String organizationName);
    
    boolean existsByOrganizationNameAndHiddenFalse(String organizationName);
}