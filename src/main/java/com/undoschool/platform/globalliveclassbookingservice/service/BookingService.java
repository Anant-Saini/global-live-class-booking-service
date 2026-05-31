package com.undoschool.platform.globalliveclassbookingservice.service;

import com.undoschool.platform.globalliveclassbookingservice.dto.responseDTOs.BookingResponseDTO;
import com.undoschool.platform.globalliveclassbookingservice.entity.*;
import com.undoschool.platform.globalliveclassbookingservice.exception.ResourceNotFoundException;
import com.undoschool.platform.globalliveclassbookingservice.exception.ScheduleConflictException;
import com.undoschool.platform.globalliveclassbookingservice.repository.BookingRepository;
import com.undoschool.platform.globalliveclassbookingservice.repository.OfferingRepository;
import com.undoschool.platform.globalliveclassbookingservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final OfferingRepository offeringRepository;
    private final UserRepository userRepository;

    @Transactional
    public BookingResponseDTO bookOffering(Long parentId, Long offeringId) {
        // 1. Authorization & Parent-Level Synchronization
        // By using a pessimistic lock on the Parent record, we ensure that
        // simultaneous booking requests for the SAME parent are processed sequentially.
        // This helps in preventing Race Conditions For Business Rules Integrity
        User parent = userRepository.findByIdWithLock(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found"));
        
        if (parent.getRole() != UserRole.PARENT) {
            throw new IllegalArgumentException("Only users with PARENT role can book offerings.");
        }

        // 2. Fetch Offering (Optimistic Locking happens on update)
        Offering offering = offeringRepository.findByIdWithDetails(offeringId)
                .orElseThrow(() -> new ResourceNotFoundException("Offering not found"));

        // 3. Business Rule Validation
        if (offering.getBookedSeats() >= offering.getTotalSeats()) {
            throw new IllegalArgumentException("Offering is fully booked.");
        }

        if (bookingRepository.existsByRegistrant_IdAndOffering_Id(parentId, offeringId)) {
            throw new IllegalArgumentException("You have already booked this offering.");
        }

        // 4. Rule 2: Time Conflict Locking (Overlap check)
        if (bookingRepository.hasScheduleConflict(parentId, offeringId)) {
            throw new ScheduleConflictException("Cannot Book: This offering's schedule overlaps with your existing bookings.");
        }

        // 5. Update State
        Booking booking = Booking.builder()
                .registrant(parent)
                .offering(offering)
                .bookingPrice(offering.getPrice())
                .currency(offering.getCurrency())
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        
        offering.setBookedSeats(offering.getBookedSeats() + 1);
        offeringRepository.save(offering); // Triggers version check

        return mapToResponse(savedBooking, ZoneId.of("UTC"));
    }

    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getBookingsByParent(Long parentId, ZoneId targetZone) {
        User parent = userRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found"));
        if (parent.getRole() != UserRole.PARENT) {
            throw new IllegalArgumentException("Only users with PARENT role can search their bookings.");
        }

        return bookingRepository.findByRegistrant_Id(parentId).stream()
                .map(booking -> mapToResponse(booking, targetZone))
                .collect(Collectors.toList());
    }

    private BookingResponseDTO mapToResponse(Booking booking, ZoneId zoneId) {
        Offering o = booking.getOffering();
        return BookingResponseDTO.builder()
                .bookingId(booking.getId())
                .offeringId(o.getId())
                .courseName(o.getCourse().getTitle())
                .offeringName(o.getName())
                .teacherName(o.getTeacher().getName())
                .bookedAt(booking.getBookedAt().withZoneSameInstant(zoneId))
                .build();
    }
}