package com.example.geartrackapi.service;

import com.example.geartrackapi.controller.quote.dto.*;
import com.example.geartrackapi.dao.model.Quote;
import com.example.geartrackapi.dao.model.QuoteMaterial;
import com.example.geartrackapi.dao.model.QuoteProductionActivity;
import com.example.geartrackapi.dao.repository.QuoteRepository;
import com.example.geartrackapi.mapper.QuoteMapper;
import com.example.geartrackapi.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuoteCrudService {

    private final QuoteRepository quoteRepository;
    private final QuoteMapper quoteMapper;

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
}