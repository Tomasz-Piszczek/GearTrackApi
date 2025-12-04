package com.example.geartrackapi.controller.quote;

import com.example.geartrackapi.controller.quote.dto.*;
import com.example.geartrackapi.service.QuoteCrudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/quotes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('SUPER_USER')")
public class QuoteController {

    private final QuoteCrudService quoteCrudService;
    @PostMapping
    public ResponseEntity<QuoteListDto> createQuote(@RequestBody CreateQuoteDto dto) {
        log.info("[createQuote] Creating quote with document number: {}", dto.getDocumentNumber());
        return ResponseEntity.ok(quoteCrudService.createQuote(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuoteListDto> updateQuote(@PathVariable UUID id, @RequestBody UpdateQuoteDto dto) {
        log.info("[updateQuote] Updating quote with UUID: {}", id);
        dto.setUuid(id);
        return ResponseEntity.ok(quoteCrudService.updateQuote(dto));
    }

    @GetMapping
    public ResponseEntity<Page<QuoteListDto>> getQuotes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {
        
        log.info("[getQuotes] Getting quotes with page: {}, size: {}, search: {}", page, size, search);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(quoteCrudService.getQuotes(search, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuoteDetailsDto> getQuote(@PathVariable UUID id) {
        log.info("[getQuote] Getting quote details with UUID: {}", id);
        return ResponseEntity.ok(quoteCrudService.getQuoteDetails(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuote(@PathVariable UUID id) {
        log.info("[deleteQuote] Deleting quote with UUID: {}", id);
        quoteCrudService.deleteQuote(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/next-number")
    public ResponseEntity<NextQuoteNumberDto> getNextQuoteNumber() {
        log.info("[getNextQuoteNumber] Getting next quote number");
        return ResponseEntity.ok(quoteCrudService.getNextQuoteNumber());
    }
}