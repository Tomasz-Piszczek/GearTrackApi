package com.example.geartrackapi.controller.urlop;

import com.example.geartrackapi.controller.urlop.dto.UrlopDto;
import com.example.geartrackapi.service.UrlopService;
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
@RequestMapping("/api/urlopy")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('SUPER_USER')")
public class UrlopController {

    private final UrlopService urlopService;

    @GetMapping
    public ResponseEntity<List<UrlopDto>> getAllUrlopy() {
        log.info("[getAllUrlopy] Getting all urlopy for organization");
        return ResponseEntity.ok(urlopService.getAllUrlopy());
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<List<UrlopDto>> getUrlopByEmployeeId(@PathVariable UUID employeeId) {
        log.info("[getUrlopByEmployeeId] Getting urlopy for employee: {}", employeeId);
        return ResponseEntity.ok(urlopService.getUrlopByEmployeeId(employeeId));
    }

    @PostMapping("/{employeeId}")
    public ResponseEntity<UrlopDto> createUrlop(
            @PathVariable UUID employeeId,
            @RequestBody UrlopDto urlopDto) {
        log.info("[createUrlop] Creating urlop for employee: {}", employeeId);
        return ResponseEntity.ok(urlopService.createUrlop(employeeId, urlopDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UrlopDto> updateUrlop(
            @PathVariable UUID id,
            @RequestBody UrlopDto urlopDto) {
        log.info("[updateUrlop] Updating urlop: {}", id);
        return ResponseEntity.ok(urlopService.updateUrlop(id, urlopDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUrlop(@PathVariable UUID id) {
        log.info("[deleteUrlop] Deleting urlop: {}", id);
        urlopService.deleteUrlop(id);
        return ResponseEntity.noContent().build();
    }

}
