package com.undoschool.platform.globalliveclassbookingservice.controller;

import com.undoschool.platform.globalliveclassbookingservice.dto.responseDTOs.OfferingWithSessionsResponseDTO;
import com.undoschool.platform.globalliveclassbookingservice.service.OfferingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ParentController {
    private final OfferingService offeringService;

    @GetMapping("/parent/offerings/available")
    public ResponseEntity<List<OfferingWithSessionsResponseDTO>> getAvailableOfferings(
            @RequestHeader(value = "X-Time-Zone", defaultValue = "UTC") ZoneId zoneId) {
        return ResponseEntity.ok(offeringService.getAvailableOfferings(zoneId));
    }
}