package com.example.geartrackapi.mapper;

import com.example.geartrackapi.controller.quote.dto.*;
import com.example.geartrackapi.dao.model.Quote;
import com.example.geartrackapi.dao.model.QuoteMaterial;
import com.example.geartrackapi.dao.model.QuoteProductionActivity;
import com.example.geartrackapi.dao.model.User;
import com.example.geartrackapi.dao.repository.UserRepository;
import com.example.geartrackapi.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class QuoteMapper {
    
    private final UserRepository userRepository;

    public Quote toEntity(CreateQuoteDto dto) {
        return Quote.builder()
                .documentNumber(dto.getDocumentNumber())
                .contractorCode(dto.getContractorCode())
                .contractorName(dto.getContractorName())
                .productCode(dto.getProductCode())
                .productName(dto.getProductName())
                .minQuantity(dto.getMinQuantity())
                .totalQuantity(dto.getTotalQuantity())
                .totalPrice(dto.getTotalPrice() != null ? java.math.BigDecimal.valueOf(dto.getTotalPrice()) : null)
                .organizationId(SecurityUtils.getCurrentOrganizationId())
                .userId(SecurityUtils.getCurrentUserId())
                .build();
    }

    public Quote toEntity(UpdateQuoteDto dto) {
        return Quote.builder()
                .id(dto.getUuid())
                .documentNumber(dto.getDocumentNumber())
                .contractorCode(dto.getContractorCode())
                .contractorName(dto.getContractorName())
                .productCode(dto.getProductCode())
                .productName(dto.getProductName())
                .minQuantity(dto.getMinQuantity())
                .totalQuantity(dto.getTotalQuantity())
                .totalPrice(dto.getTotalPrice() != null ? java.math.BigDecimal.valueOf(dto.getTotalPrice()) : null)
                .build();
    }

    public Quote updateEntity(Quote existing, UpdateQuoteDto dto) {
        return Quote.builder()
                .id(existing.getId())
                .documentNumber(dto.getDocumentNumber())
                .contractorCode(dto.getContractorCode())
                .contractorName(dto.getContractorName())
                .productCode(dto.getProductCode())
                .productName(dto.getProductName())
                .minQuantity(dto.getMinQuantity())
                .totalQuantity(dto.getTotalQuantity())
                .totalPrice(dto.getTotalPrice() != null ? java.math.BigDecimal.valueOf(dto.getTotalPrice()) : null)
                .organizationId(existing.getOrganizationId())
                .userId(existing.getUserId())
                .build();
    }

    public QuoteListDto toListDto(Quote entity) {
        String createdByEmail = null;
        if (entity.getUserId() != null) {
            createdByEmail = userRepository.findById(entity.getUserId())
                    .map(User::getEmail)
                    .orElse(null);
        }
        
        return QuoteListDto.builder()
                .uuid(entity.getId())
                .documentNumber(entity.getDocumentNumber())
                .contractorCode(entity.getContractorCode())
                .contractorName(entity.getContractorName())
                .productCode(entity.getProductCode())
                .productName(entity.getProductName())
                .minQuantity(entity.getMinQuantity())
                .totalQuantity(entity.getTotalQuantity())
                .totalPrice(entity.getTotalPrice() != null ? entity.getTotalPrice().doubleValue() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getUserId())
                .createdByEmail(createdByEmail)
                .build();
    }

    public QuoteDetailsDto toDetailsDto(Quote entity) {
        return QuoteDetailsDto.builder()
                .uuid(entity.getId())
                .documentNumber(entity.getDocumentNumber())
                .contractorCode(entity.getContractorCode())
                .contractorName(entity.getContractorName())
                .productCode(entity.getProductCode())
                .productName(entity.getProductName())
                .minQuantity(entity.getMinQuantity())
                .totalQuantity(entity.getTotalQuantity())
                .totalPrice(entity.getTotalPrice() != null ? entity.getTotalPrice().doubleValue() : null)
                .materials(entity.getMaterials().stream().map(this::toMaterialDto).collect(Collectors.toList()))
                .productionActivities(entity.getProductionActivities().stream().map(this::toProductionActivityDto).collect(Collectors.toList()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public List<QuoteMaterial> toMaterialEntities(List<QuoteMaterialDto> dtos, Quote quote) {
        return dtos.stream()
                .map(dto -> toMaterialEntity(dto, quote))
                .collect(Collectors.toList());
    }

    public QuoteMaterial toMaterialEntity(QuoteMaterialDto dto, Quote quote) {
        return QuoteMaterial.builder()
                .id(dto.getUuid())
                .quote(quote)
                .organizationId(quote.getOrganizationId())
                .name(dto.getName())
                .purchasePrice(dto.getPurchasePrice())
                .marginPercent(dto.getMarginPercent())
                .marginPln(dto.getMarginPln())
                .quantity(dto.getQuantity())
                .ignoreMinQuantity(dto.getIgnoreMinQuantity())
                .build();
    }

    public QuoteMaterialDto toMaterialDto(QuoteMaterial entity) {
        return QuoteMaterialDto.builder()
                .uuid(entity.getId())
                .name(entity.getName())
                .purchasePrice(entity.getPurchasePrice())
                .marginPercent(entity.getMarginPercent())
                .marginPln(entity.getMarginPln())
                .quantity(entity.getQuantity())
                .ignoreMinQuantity(entity.getIgnoreMinQuantity())
                .build();
    }

    public List<QuoteProductionActivity> toProductionActivityEntities(List<QuoteProductionActivityDto> dtos, Quote quote) {
        return dtos.stream()
                .map(dto -> toProductionActivityEntity(dto, quote))
                .collect(Collectors.toList());
    }

    public QuoteProductionActivity toProductionActivityEntity(QuoteProductionActivityDto dto, Quote quote) {
        Integer totalMinutes = (dto.getWorkTimeHours() * 60) + dto.getWorkTimeMinutes();
        
        return QuoteProductionActivity.builder()
                .id(dto.getUuid())
                .quote(quote)
                .organizationId(quote.getOrganizationId())
                .name(dto.getName())
                .workTimeMinutes(totalMinutes)
                .price(dto.getPrice())
                .marginPercent(dto.getMarginPercent())
                .marginPln(dto.getMarginPln())
                .ignoreMinQuantity(dto.getIgnoreMinQuantity())
                .build();
    }

    public QuoteProductionActivityDto toProductionActivityDto(QuoteProductionActivity entity) {
        Integer hours = entity.getWorkTimeMinutes() / 60;
        Integer minutes = entity.getWorkTimeMinutes() % 60;
        
        return QuoteProductionActivityDto.builder()
                .uuid(entity.getId())
                .name(entity.getName())
                .workTimeHours(hours)
                .workTimeMinutes(minutes)
                .price(entity.getPrice())
                .marginPercent(entity.getMarginPercent())
                .marginPln(entity.getMarginPln())
                .ignoreMinQuantity(entity.getIgnoreMinQuantity())
                .build();
    }
}