package com.undoschool.platform.globalliveclassbookingservice.service;

import com.undoschool.platform.globalliveclassbookingservice.dto.responseDTOs.BookingResponseDTO;
import com.undoschool.platform.globalliveclassbookingservice.entity.*;
import com.undoschool.platform.globalliveclassbookingservice.exception.ResourceNotFoundException;
import com.undoschool.platform.globalliveclassbookingservice.exception.ScheduleConflictException;
import com.undoschool.platform.globalliveclassbookingservice.repository.BookingRepository;
import com.undoschool.platform.globalliveclassbookingservice.repository.OfferingRepository;
import com.undoschool.platform.globalliveclassbookingservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private OfferingRepository offeringRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    private User parent;
    private Offering offering;
    private final Long parentId = 1L;
    private final Long offeringId = 100L;

    @BeforeEach
    void setUp() {
        parent = User.builder().id(parentId).name("John Doe").role(UserRole.PARENT).build();
        
        Course course = Course.builder().title("Java Unit Testing").build();
        User teacher = User.builder().name("Prof. Mock").build();
        
        offering = Offering.builder()
                .id(offeringId)
                .course(course)
                .teacher(teacher)
                .name("Summer Batch")
                .totalSeats(10)
                .bookedSeats(5)
                .price(new BigDecimal("100.00"))
                .currency("USD")
                .build();
    }

    @Test
    @DisplayName("Should successfully book an offering when all conditions are met")
    void bookOffering_Success() {
        // Arrange
        when(userRepository.findByIdWithLock(parentId)).thenReturn(Optional.of(parent));
        when(offeringRepository.findByIdWithDetails(offeringId)).thenReturn(Optional.of(offering));
        when(bookingRepository.existsByRegistrant_IdAndOffering_Id(parentId, offeringId)).thenReturn(false);
        when(bookingRepository.hasScheduleConflict(parentId, offeringId)).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking b = invocation.getArgument(0);
            b.setId(500L); // Simulate DB ID generation
            return b;
        });

        // Act
        BookingResponseDTO response = bookingService.bookOffering(parentId, offeringId);

        // Assert
        assertNotNull(response);
        assertEquals(500L, response.bookingId());
        assertEquals("Java Unit Testing", response.courseName());
        assertEquals(6, offering.getBookedSeats()); // Verified seat increment
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(offeringRepository, times(1)).save(offering);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when parent does not exist")
    void bookOffering_ParentNotFound() {
        // Arrange
        when(userRepository.findByIdWithLock(parentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> bookingService.bookOffering(parentId, offeringId));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when user role is not PARENT")
    void bookOffering_UserNotParent() {
        // Arrange
        parent.setRole(UserRole.TEACHER);
        when(userRepository.findByIdWithLock(parentId)).thenReturn(Optional.of(parent));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> bookingService.bookOffering(parentId, offeringId));
        assertTrue(exception.getMessage().contains("Only users with PARENT role can book offerings."));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when offering does not exist")
    void bookOffering_OfferingNotFound() {
        // Arrange
        when(userRepository.findByIdWithLock(parentId)).thenReturn(Optional.of(parent));
        when(offeringRepository.findByIdWithDetails(offeringId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> bookingService.bookOffering(parentId, offeringId));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when offering is fully booked")
    void bookOffering_FullyBooked() {
        // Arrange
        offering.setBookedSeats(10); // Match totalSeats
        when(userRepository.findByIdWithLock(parentId)).thenReturn(Optional.of(parent));
        when(offeringRepository.findByIdWithDetails(offeringId)).thenReturn(Optional.of(offering));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> bookingService.bookOffering(parentId, offeringId));
        assertEquals("Offering is fully booked.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for duplicate booking")
    void bookOffering_DuplicateBooking() {
        // Arrange
        when(userRepository.findByIdWithLock(parentId)).thenReturn(Optional.of(parent));
        when(offeringRepository.findByIdWithDetails(offeringId)).thenReturn(Optional.of(offering));
        when(bookingRepository.existsByRegistrant_IdAndOffering_Id(parentId, offeringId)).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> bookingService.bookOffering(parentId, offeringId));
        assertEquals("You have already booked this offering.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw ScheduleConflictException when session timings overlap")
    void bookOffering_ScheduleConflict() {
        // Arrange
        when(userRepository.findByIdWithLock(parentId)).thenReturn(Optional.of(parent));
        when(offeringRepository.findByIdWithDetails(offeringId)).thenReturn(Optional.of(offering));
        when(bookingRepository.existsByRegistrant_IdAndOffering_Id(parentId, offeringId)).thenReturn(false);
        when(bookingRepository.hasScheduleConflict(parentId, offeringId)).thenReturn(true);

        // Act & Assert
        ScheduleConflictException exception = assertThrows(ScheduleConflictException.class, 
                () -> bookingService.bookOffering(parentId, offeringId));
        assertTrue(exception.getMessage().contains("overlaps with your existing bookings"));
    }

    @Test
    @DisplayName("Should successfully retrieve bookings for a valid parent")
    void getBookingsByParent_Success() {
        // Assume
        java.time.ZoneId zoneId = java.time.ZoneId.of("Asia/Kolkata");
        Booking mockBooking = Booking.builder()
                .id(1L)
                .registrant(parent)
                .offering(offering)
                .build();

        when(userRepository.findById(parentId)).thenReturn(Optional.of(parent));
        when(bookingRepository.findByRegistrant_Id(parentId)).thenReturn(List.of(mockBooking));

        // Assign
        List<BookingResponseDTO> result = bookingService.getBookingsByParent(parentId, zoneId);

        // Assert
        assertEquals(1, result.size());
        verify(bookingRepository, times(1)).findByRegistrant_Id(parentId);
    }
}