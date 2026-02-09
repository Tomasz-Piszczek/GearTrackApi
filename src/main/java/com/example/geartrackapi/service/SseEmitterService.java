package com.example.geartrackapi.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class SseEmitterService {

    private final Map<UUID, List<SseEmitter>> emittersByOrganization = new ConcurrentHashMap<>();

    public SseEmitter createEmitter(UUID organizationId) {
        SseEmitter emitter = new SseEmitter(0L);

        emittersByOrganization
            .computeIfAbsent(organizationId, k -> new CopyOnWriteArrayList<>())
            .add(emitter);

        emitter.onCompletion(() -> removeEmitter(organizationId, emitter));
        emitter.onTimeout(() -> removeEmitter(organizationId, emitter));
        emitter.onError((e) -> removeEmitter(organizationId, emitter));

        return emitter;
    }

    public void emitEvent(UUID organizationId, String eventType, Object data) {
        List<SseEmitter> emitters = emittersByOrganization.get(organizationId);
        if (emitters == null || emitters.isEmpty()) {
            return;
        }

        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                    .name(eventType)
                    .data(data));
            } catch (IOException e) {
                log.warn("Failed to send SSE event to client, marking for removal");
                deadEmitters.add(emitter);
            }
        }

        emitters.removeAll(deadEmitters);
    }

    private void removeEmitter(UUID organizationId, SseEmitter emitter) {
        List<SseEmitter> emitters = emittersByOrganization.get(organizationId);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                emittersByOrganization.remove(organizationId);
            }
        }
    }
}
