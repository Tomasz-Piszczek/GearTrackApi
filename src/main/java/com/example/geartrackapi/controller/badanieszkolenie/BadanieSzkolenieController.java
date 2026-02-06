package com.example.geartrackapi.controller.badanieszkolenie;

import com.example.geartrackapi.controller.badanieszkolenie.dto.BadanieSzkolenieDto;
import com.example.geartrackapi.service.BadanieSzkolenieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/badania-szkolenia")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('SUPER_USER')")
public class BadanieSzkolenieController {

    private final BadanieSzkolenieService badanieSzkolenieService;

    @GetMapping
    public ResponseEntity<List<BadanieSzkolenieDto>> getAllBadaniaSzkolenia() {
        log.info("[getAllBadaniaSzkolenia] Getting all badania szkolenia for organization");
        return ResponseEntity.ok(badanieSzkolenieService.getAllBadaniaSzkolenia());
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<List<BadanieSzkolenieDto>> getBadaniaSzkoleniaByEmployeeId(@PathVariable UUID employeeId) {
        log.info("[getBadaniaSzkoleniaByEmployeeId] Getting badania szkolenia for employee: {}", employeeId);
        return ResponseEntity.ok(badanieSzkolenieService.getBadaniaSzkoleniaByEmployeeId(employeeId));
    }

    @PostMapping("/{employeeId}")
    public ResponseEntity<BadanieSzkolenieDto> createBadanieSzkolenie(
            @PathVariable UUID employeeId,
            @RequestBody BadanieSzkolenieDto dto) {
        log.info("[createBadanieSzkolenie] Creating badanie szkolenie for employee: {}", employeeId);
        return ResponseEntity.ok(badanieSzkolenieService.createBadanieSzkolenie(employeeId, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BadanieSzkolenieDto> updateBadanieSzkolenie(
            @PathVariable UUID id,
            @RequestBody BadanieSzkolenieDto dto) {
        log.info("[updateBadanieSzkolenie] Updating badanie szkolenie: {}", id);
        return ResponseEntity.ok(badanieSzkolenieService.updateBadanieSzkolenie(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBadanieSzkolenie(@PathVariable UUID id) {
        log.info("[deleteBadanieSzkolenie] Deleting badanie szkolenie: {}", id);
        badanieSzkolenieService.deleteBadanieSzkolenie(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        log.info("[getCategories] Getting all badania szkolenia categories");
        return ResponseEntity.ok(badanieSzkolenieService.getCategories());
    }

}
