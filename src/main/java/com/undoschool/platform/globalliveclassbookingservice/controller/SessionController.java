package com.undoschool.platform.globalliveclassbookingservice.controller;

import com.undoschool.platform.globalliveclassbookingservice.dto.requestDTOs.SessionRequestDTO;
import com.undoschool.platform.globalliveclassbookingservice.dto.responseDTOs.SessionResponseDTO;
import com.undoschool.platform.globalliveclassbookingservice.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
public class SessionController {

    private final SessionService sessionService;

    /**
     * Adds a list of sessions to an existing offering.
     * Fails if the teacher is not the owner or has schedule conflicts.
     */
    @PostMapping("/offerings/{offeringId}/sessions")
    public ResponseEntity<List<SessionResponseDTO>> addSessions(
            @PathVariable Long offeringId,
            @RequestHeader("X-Teacher-Id") Long teacherId,
            @RequestBody @Valid List<SessionRequestDTO> sessionRequests) {
        
        List<SessionResponseDTO> sessions = sessionService.addSessionsToOffering(offeringId, teacherId, sessionRequests);
        return new ResponseEntity<>(sessions, HttpStatus.CREATED);
    }
}