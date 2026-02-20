package com.example.geartrackapi.service;

import com.example.geartrackapi.controller.quote.dto.*;
import com.example.geartrackapi.dao.model.Quote;
import com.example.geartrackapi.dao.model.QuoteAttachment;
import com.example.geartrackapi.dao.model.QuoteMaterial;
import com.example.geartrackapi.dao.model.QuoteProductionActivity;
import com.example.geartrackapi.dao.repository.QuoteAttachmentRepository;
import com.example.geartrackapi.dao.repository.QuoteRepository;
import com.example.geartrackapi.mapper.QuoteMapper;
import com.example.geartrackapi.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuoteCrudService {

    private final QuoteRepository quoteRepository;
    private final QuoteAttachmentRepository attachmentRepository;
    private final QuoteMapper quoteMapper;

    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
            "application/pdf",
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/gif",
            "image/bmp",
            "image/webp",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/plain"
    );

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    @Transactional
    public QuoteListDto createQuote(CreateQuoteDto dto) {
        Quote quote = quoteMapper.toEntity(dto);
        
        List<QuoteMaterial> materials = quoteMapper.toMaterialEntities(dto.getMaterials(), quote);
        List<QuoteProductionActivity> activities = quoteMapper.toProductionActivityEntities(dto.getProductionActivities(), quote);
        
        quote.setMaterials(materials);
        quote.setProductionActivities(activities);
        
        Quote savedQuote = quoteRepository.save(quote);
        return quoteMapper.toListDto(savedQuote);
    }

    @Transactional
    public QuoteListDto updateQuote(UpdateQuoteDto dto) {
        Quote existingQuote = quoteRepository.findByIdAndOrganizationIdAndHiddenFalse(dto.getUuid(), SecurityUtils.getCurrentOrganizationId())
                .orElseThrow(() -> new RuntimeException("Quote not found with UUID: " + dto.getUuid()));
        
        Quote quote = quoteMapper.updateEntity(existingQuote, dto);

        updateQuoteMaterials(quote, dto.getMaterials());
        updateQuoteProductionActivities(quote, dto.getProductionActivities());

        Quote savedQuote = quoteRepository.save(quote);
        return quoteMapper.toListDto(savedQuote);
    }
    
    private void updateQuoteMaterials(Quote quote, List<QuoteMaterialDto> newMaterials) {
        quote.getMaterials().clear();
        List<QuoteMaterial> materials = quoteMapper.toMaterialEntities(newMaterials, quote);
        quote.getMaterials().addAll(materials);
    }
    
    private void updateQuoteProductionActivities(Quote quote, List<QuoteProductionActivityDto> newActivities) {
        quote.getProductionActivities().clear();
        List<QuoteProductionActivity> activities = quoteMapper.toProductionActivityEntities(newActivities, quote);
        quote.getProductionActivities().addAll(activities);
    }

    public Page<QuoteListDto> getQuotes(String search, UUID createdBy, Pageable pageable) {
        Page<Quote> quotePage = quoteRepository.findBySearchAndOrganizationAndCreatedBy(search, SecurityUtils.getCurrentOrganizationId(), createdBy, pageable);
        List<QuoteListDto> dtos = quotePage.getContent().stream()
                .map(quoteMapper::toListDto)
                .collect(Collectors.toList());
        
        return new PageImpl<>(dtos, pageable, quotePage.getTotalElements());
    }

    public QuoteDetailsDto getQuoteDetails(UUID id) {
        Quote quote = quoteRepository.findByIdAndOrganizationIdAndHiddenFalse(id, SecurityUtils.getCurrentOrganizationId())
                .orElseThrow(() -> new RuntimeException("Quote not found with UUID: " + id));
        return quoteMapper.toDetailsDto(quote);
    }

    public void deleteQuote(UUID id) {
        Quote quote = quoteRepository.findByIdAndOrganizationIdAndHiddenFalse(id, SecurityUtils.getCurrentOrganizationId())
                .orElseThrow(() -> new RuntimeException("Quote not found with UUID: " + id));
        quote.setHidden(true);
        quoteRepository.save(quote);
    }

    public NextQuoteNumberDto getNextQuoteNumber() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime startOfNextMonth = startOfMonth.plusMonths(1);

        String monthYear = now.format(DateTimeFormatter.ofPattern("MM/yyyy"));
        Long count = quoteRepository.countQuotesForMonthAndOrganization(startOfMonth, startOfNextMonth, monthYear, SecurityUtils.getCurrentOrganizationId());

        Integer nextNumber = count.intValue() + 1;
        String nextQuoteNumber = String.format("OFE/%d/%02d/%d", nextNumber, now.getMonthValue(), now.getYear());

        return NextQuoteNumberDto.builder()
                .nextQuoteNumber(nextQuoteNumber)
                .sequenceNumber(nextNumber)
                .month(now.getMonthValue())
                .year(now.getYear())
                .build();
    }

    @Transactional
    public QuoteAttachmentDto uploadAttachment(UUID quoteId, MultipartFile file) {
        Quote quote = quoteRepository.findByIdAndOrganizationIdAndHiddenFalse(quoteId, SecurityUtils.getCurrentOrganizationId())
                .orElseThrow(() -> new RuntimeException("Quote not found with UUID: " + quoteId));

        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("File size exceeds maximum allowed size of 10MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_FILE_TYPES.contains(contentType)) {
            throw new RuntimeException("File type not allowed. Supported types: PDF, images (PNG, JPG, GIF, BMP, WEBP), Word, Excel, and text files");
        }

        try {
            QuoteAttachment attachment = QuoteAttachment.builder()
                    .quote(quote)
                    .fileName(file.getOriginalFilename())
                    .fileType(contentType)
                    .fileSize(file.getSize())
                    .fileData(file.getBytes())
                    .organizationId(SecurityUtils.getCurrentOrganizationId())
                    .build();

            QuoteAttachment savedAttachment = attachmentRepository.save(attachment);
            return quoteMapper.toAttachmentDto(savedAttachment);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file data", e);
        }
    }

    public QuoteAttachment getAttachment(UUID quoteId, UUID attachmentId) {
        QuoteAttachment attachment = attachmentRepository.findByIdAndOrganizationIdAndHiddenFalse(attachmentId, SecurityUtils.getCurrentOrganizationId())
                .orElseThrow(() -> new RuntimeException("Attachment not found with UUID: " + attachmentId));

        if (!attachment.getQuote().getId().equals(quoteId)) {
            throw new RuntimeException("Attachment does not belong to the specified quote");
        }

        return attachment;
    }

    @Transactional
    public void deleteAttachment(UUID quoteId, UUID attachmentId) {
        QuoteAttachment attachment = attachmentRepository.findByIdAndOrganizationIdAndHiddenFalse(attachmentId, SecurityUtils.getCurrentOrganizationId())
                .orElseThrow(() -> new RuntimeException("Attachment not found with UUID: " + attachmentId));

        if (!attachment.getQuote().getId().equals(quoteId)) {
            throw new RuntimeException("Attachment does not belong to the specified quote");
        }

        attachment.setHidden(true);
        attachmentRepository.save(attachment);
    }
}