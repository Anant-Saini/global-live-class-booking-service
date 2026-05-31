package com.undoschool.platform.globalliveclassbookingservice.controller;

import com.undoschool.platform.globalliveclassbookingservice.dto.requestDTOs.BookingRequestDTO;
import com.undoschool.platform.globalliveclassbookingservice.dto.responseDTOs.BookingResponseDTO;
import com.undoschool.platform.globalliveclassbookingservice.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    /**
     * Atomic Booking with Conflict Detection and Concurrency Handling.
     * The parent books the entire offering via the body, and their identity is verified via header.
     */
    @PostMapping("/bookings")
    public ResponseEntity<BookingResponseDTO> bookOffering(
            @RequestHeader(name = "X-Parent-Id") Long parentId,
            @Valid @RequestBody BookingRequestDTO request) {
        
        BookingResponseDTO response = bookingService.bookOffering(parentId, request.offeringId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingResponseDTO>> getMyBookings(
            @RequestHeader(name = "X-Parent-Id") Long parentId,
            @RequestHeader(name = "X-Time-Zone", defaultValue = "UTC") ZoneId zoneId) {
        List<BookingResponseDTO> response = bookingService.getBookingsByParent(parentId, zoneId);
        return ResponseEntity.ok(response);
    }
}