package com.example.geartrackapi.controller.quote;

import com.example.geartrackapi.controller.quote.dto.*;
import com.example.geartrackapi.service.QuoteCrudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UUID createdBy) {
        
        log.info("[getQuotes] Getting quotes with page: {}, size: {}, search: {}, createdBy: {}", page, size, search, createdBy);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(quoteCrudService.getQuotes(search, createdBy, pageable));
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

    @PostMapping("/{id}/attachments")
    public ResponseEntity<QuoteAttachmentDto> uploadAttachment(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        log.info("[uploadAttachment] Uploading attachment for quote: {}, fileName: {}", id, file.getOriginalFilename());
        return ResponseEntity.ok(quoteCrudService.uploadAttachment(id, file));
    }

    @GetMapping("/{id}/attachments/{attachmentId}")
    public ResponseEntity<Resource> downloadAttachment(
            @PathVariable UUID id,
            @PathVariable UUID attachmentId) {
        log.info("[downloadAttachment] Downloading attachment: {} for quote: {}", attachmentId, id);
        var attachment = quoteCrudService.getAttachment(id, attachmentId);

        ByteArrayResource resource = new ByteArrayResource(attachment.getFileData());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(attachment.getFileType()))
                .contentLength(attachment.getFileSize())
                .body(resource);
    }

    @DeleteMapping("/{id}/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(
            @PathVariable UUID id,
            @PathVariable UUID attachmentId) {
        log.info("[deleteAttachment] Deleting attachment: {} for quote: {}", attachmentId, id);
        quoteCrudService.deleteAttachment(id, attachmentId);
        return ResponseEntity.noContent().build();
    }
}