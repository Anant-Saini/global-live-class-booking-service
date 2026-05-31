package com.undoschool.platform.globalliveclassbookingservice.controller;

import com.undoschool.platform.globalliveclassbookingservice.dto.requestDTOs.OfferingRequestDTO;
import com.undoschool.platform.globalliveclassbookingservice.dto.responseDTOs.OfferingResponseDTO;
import com.undoschool.platform.globalliveclassbookingservice.dto.responseDTOs.OfferingWithSessionsResponseDTO;
import com.undoschool.platform.globalliveclassbookingservice.service.OfferingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OfferingController {
    private final OfferingService offeringService;

    @PostMapping("/offerings")
    public ResponseEntity<OfferingResponseDTO> createOffering(
            @RequestHeader("X-Teacher-Id") Long teacherId,
            @Valid @RequestBody OfferingRequestDTO dto) {
        return new ResponseEntity<>(offeringService.createOffering(teacherId, dto), HttpStatus.CREATED);
    }

    @GetMapping("/offerings/teacher/{teacherId}")
    public ResponseEntity<List<OfferingResponseDTO>> getTeacherOfferings(@PathVariable Long teacherId) {
        // Logic to fetch and map all offerings for a specific teacher
        return ResponseEntity.ok(offeringService.getOfferingsByTeacher(teacherId));
    }

    @GetMapping("/offerings/{id}")
    public ResponseEntity<OfferingWithSessionsResponseDTO> getOfferingById(
            @PathVariable("id") Long offeringId,
            @RequestHeader(value = "X-Time-Zone", defaultValue = "UTC") String zoneId) {
        return ResponseEntity.ok(offeringService.getOfferingWithSessions(offeringId, zoneId));
    }

    @GetMapping("/offerings/upcoming")
    public ResponseEntity<List<OfferingWithSessionsResponseDTO>> getUpcomingOfferings(
            @RequestHeader("X-Teacher-Id") Long teacherId,
            @RequestHeader(value = "X-Time-Zone", defaultValue = "UTC") String zoneId) {
        return ResponseEntity.ok(offeringService.getUpcomingOfferingsForTeacher(teacherId, zoneId));
    }
}
