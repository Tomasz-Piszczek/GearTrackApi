package com.example.geartrackapi.dao.repository;

import com.example.geartrackapi.dao.model.QuoteAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuoteAttachmentRepository extends JpaRepository<QuoteAttachment, UUID> {

    List<QuoteAttachment> findByQuoteIdAndHiddenFalse(UUID quoteId);

    Optional<QuoteAttachment> findByIdAndOrganizationIdAndHiddenFalse(UUID id, UUID organizationId);
}
