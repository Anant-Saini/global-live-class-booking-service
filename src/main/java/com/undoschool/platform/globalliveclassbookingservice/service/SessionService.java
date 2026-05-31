package com.undoschool.platform.globalliveclassbookingservice.service;

import com.undoschool.platform.globalliveclassbookingservice.dto.requestDTOs.SessionRequestDTO;
import com.undoschool.platform.globalliveclassbookingservice.dto.responseDTOs.SessionResponseDTO;
import com.undoschool.platform.globalliveclassbookingservice.entity.Offering;
import com.undoschool.platform.globalliveclassbookingservice.entity.Session;
import com.undoschool.platform.globalliveclassbookingservice.exception.ResourceNotFoundException;
import com.undoschool.platform.globalliveclassbookingservice.exception.ScheduleConflictException;
import com.undoschool.platform.globalliveclassbookingservice.repository.OfferingRepository;
import com.undoschool.platform.globalliveclassbookingservice.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final OfferingRepository offeringRepository;

    @Transactional
    public List<SessionResponseDTO> addSessionsToOffering(Long offeringId, Long teacherId, List<SessionRequestDTO> sessionRequests) {
        // 1. Fail Fast: Check if Offering exists
        Offering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() -> new ResourceNotFoundException("Offering not found with id: " + offeringId));

        // 2. Fail Fast: Authorization check (Does this teacher own the offering?)
        if (!offering.getTeacher().getId().equals(teacherId)) {
            throw new IllegalArgumentException("You are not authorised to add sessions to this offering");
        }

        // 3. Conflict Detection: Check each new session against the teacher's existing schedule
        hasScheduleConflicts(teacherId, sessionRequests);

        // 4. Persistence: Convert DTOs to Entities and save
        List<Session> sessions = sessionRequests.stream()
                .map(req -> Session.builder()
                        .offering(offering)
                        .teacher(offering.getTeacher())
                        .startTime(req.startTime())
                        .endTime(req.endTime())
                        .build())
                .collect(Collectors.toList());

        return sessionRepository.saveAll(sessions).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private void hasScheduleConflicts(Long teacherId, List<SessionRequestDTO> sessionRequests) {
        for (SessionRequestDTO request : sessionRequests) {

            boolean isOverlapping = sessionRepository.existsTeacherOverlap(
                    teacherId,
                    request.startTime(),
                    request.endTime()
            );

            if (isOverlapping) {
                throw new ScheduleConflictException(String.format(
                        "Teacher has an existing session that overlaps with the requested time: %s to %s",
                        request.startTime(), request.endTime()));
            }
        }
    }

    private SessionResponseDTO mapToResponseDTO(Session session) {
        return SessionResponseDTO.builder()
                .id(session.getId())
                .offeringId(session.getOffering().getId())
                .teacherId(session.getTeacher().getId())
                .teacherName(session.getTeacher().getName()) // Assuming getFullName() exists in User entity
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .build();
    }
}