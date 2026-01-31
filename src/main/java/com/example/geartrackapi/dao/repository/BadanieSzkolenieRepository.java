package com.example.geartrackapi.dao.repository;

import com.example.geartrackapi.dao.model.BadanieSzkolenie;
import com.example.geartrackapi.dao.model.BadanieSzkolenieStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BadanieSzkolenieRepository extends JpaRepository<BadanieSzkolenie, UUID> {

    List<BadanieSzkolenie> findByEmployeeIdAndOrganizationIdAndHiddenFalse(UUID employeeId, UUID organizationId);

    List<BadanieSzkolenie> findByOrganizationIdAndHiddenFalse(UUID organizationId);

    Optional<BadanieSzkolenie> findByIdAndHiddenFalse(UUID id);

    @Query("SELECT DISTINCT b.category FROM BadanieSzkolenie b WHERE b.organizationId = :organizationId AND b.hidden = false")
    List<String> findDistinctCategoriesByOrganizationId(UUID organizationId);
}
