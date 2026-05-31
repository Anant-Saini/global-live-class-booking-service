package com.undoschool.platform.globalliveclassbookingservice.service;

import com.undoschool.platform.globalliveclassbookingservice.dto.requestDTOs.OfferingRequestDTO;
import com.undoschool.platform.globalliveclassbookingservice.dto.responseDTOs.OfferingResponseDTO;
import com.undoschool.platform.globalliveclassbookingservice.dto.responseDTOs.OfferingWithSessionsResponseDTO;
import com.undoschool.platform.globalliveclassbookingservice.dto.responseDTOs.SessionResponseDTO;
import com.undoschool.platform.globalliveclassbookingservice.entity.*;
import com.undoschool.platform.globalliveclassbookingservice.exception.ResourceNotFoundException;
import com.undoschool.platform.globalliveclassbookingservice.repository.CourseRepository;
import com.undoschool.platform.globalliveclassbookingservice.repository.OfferingRepository;
import com.undoschool.platform.globalliveclassbookingservice.repository.SessionRepository;
import com.undoschool.platform.globalliveclassbookingservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.Map;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OfferingService {
    private final OfferingRepository offeringRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public OfferingResponseDTO createOffering(Long teacherId, OfferingRequestDTO dto) {
        Course course = courseRepository.findById(dto.courseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        if (teacher.getRole() != UserRole.TEACHER) {
            throw new IllegalArgumentException("User with ID " + teacher.getId() + " is a " + teacher.getRole() + ". " +
                    "Only users with the TEACHER role can create offerings.");
        }

        Offering offering = Offering.builder()
                .course(course)
                .teacher(teacher)
                .name(dto.name())
                .price(dto.price())
                .currency(dto.currency())
                .totalSeats(dto.totalSeats())
                .bookedSeats(0) // Explicitly start at 0
                .build();

        Offering saved = offeringRepository.save(offering);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<OfferingResponseDTO> getOfferingsByTeacher(Long teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + teacherId));

        if (teacher.getRole() != UserRole.TEACHER) {
            throw new IllegalArgumentException("User with ID " + teacherId + " is not authorized as a TEACHER");
        }

        List<Offering> offerings = offeringRepository.findByTeacherIdWithDetails(teacherId);
        return offerings.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OfferingWithSessionsResponseDTO getOfferingWithSessions(Long offeringId, String zoneStr) {
        // 1. Fail Fast: Check if the offering exists
        Offering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() -> new ResourceNotFoundException("Offering not found with id: " + offeringId));
        // 2. Get TimeZone
        ZoneId targetZone = null;
        try {
            targetZone = ZoneId.of(zoneStr);
            
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Timezone ID Header");
        }
        // 3. Map to DTO
        List<Session> sessions = sessionRepository.findByOfferingId(offeringId);
        ZoneId finalTargetZone = targetZone;
        List<SessionResponseDTO> sessionDTOs = sessions.stream()
                .map(s -> mapToSessionResponseDTO(s, finalTargetZone))
                .collect(Collectors.toList());

        return mapToOfferingWithSessionsDTO(offering, sessionDTOs);
    }

    @Transactional(readOnly = true)
    public List<OfferingWithSessionsResponseDTO> getUpcomingOfferingsForTeacher(Long teacherId, String zoneStr) {
        ZoneId targetZone = null;
        
        try {
            targetZone = ZoneId.of(zoneStr);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Timezone ID Header");
        }
        
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));

        List<Offering> offerings = offeringRepository.findUpcomingByTeacher(teacherId, now);
        List<Long> offeringIds = offerings.stream().map(Offering::getId).collect(Collectors.toList());
        
        // Fetch all sessions for all offerings in one query to avoid N+1
        List<Session> allSessions = sessionRepository.findByOfferingIdIn(offeringIds);
        
        // Group sessions by offering ID
        Map<Long, List<Session>> sessionsByOffering = allSessions.stream()
                .collect(Collectors.groupingBy(s -> s.getOffering().getId()));

        ZoneId finalTargetZone = targetZone;
        return offerings.stream()
                .map(o -> {
                    List<Session> sessions = sessionsByOffering.getOrDefault(o.getId(), List.of());
                    List<SessionResponseDTO> sessionDTOs = sessions.stream()
                            .map(s -> mapToSessionResponseDTO(s, finalTargetZone))
                            .collect(Collectors.toList());
                    return mapToOfferingWithSessionsDTO(o, sessionDTOs);
                })
                .collect(Collectors.toList());
    }

    private OfferingResponseDTO mapToResponse(Offering entity) {
        return new OfferingResponseDTO(
                entity.getId(),
                entity.getCourse().getTitle(),
                entity.getTeacher().getName(),
                entity.getName(),
                entity.getPrice(),
                entity.getCurrency(),
                entity.getTotalSeats(),
                entity.getBookedSeats()
        );
    }

    private SessionResponseDTO mapToSessionResponseDTO(Session session, ZoneId zoneId) {
        return SessionResponseDTO.builder()
                .id(session.getId())
                .offeringId(session.getOffering().getId())
                .teacherId(session.getTeacher().getId())
                .teacherName(session.getTeacher().getName())
                .startTime(session.getStartTime().withZoneSameInstant(zoneId))
                .endTime(session.getEndTime().withZoneSameInstant(zoneId))
                .build();
    }

    private OfferingWithSessionsResponseDTO mapToOfferingWithSessionsDTO(Offering offering, List<SessionResponseDTO> sessions) {
        return OfferingWithSessionsResponseDTO.builder()
                .id(offering.getId())
                .courseName(offering.getCourse().getTitle())
                .offeringName(offering.getName())
                .teacherName(offering.getTeacher().getName())
                .maxSeats(offering.getTotalSeats())
                .bookedSeats(offering.getBookedSeats())
                .sessions(sessions)
                .build();
    }
}
