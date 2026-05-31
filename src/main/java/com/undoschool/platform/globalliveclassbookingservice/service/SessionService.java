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
import java.util.Comparator;

import java.time.ZoneOffset;
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

        // 3. Normalization & Conflict Detection: Convert to UTC and check conflicts
        List<SessionRequestDTO> normalizedRequests = sessionRequests.stream()
                .map(req -> new SessionRequestDTO(
                        req.startTime().withZoneSameInstant(ZoneOffset.UTC),
                        req.endTime().withZoneSameInstant(ZoneOffset.UTC)))
                .collect(Collectors.toList());

        hasScheduleConflicts(teacherId, normalizedRequests);

        // 4. Persistence: Convert DTOs to Entities and save
        List<Session> sessions = normalizedRequests.stream()
                .map(req -> mapToEntity(offering, req))
                .collect(Collectors.toList());

        return sessionRepository.saveAll(sessions).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private void hasScheduleConflicts(Long teacherId, List<SessionRequestDTO> sessionRequests) {
        // 1. Internal Check: Check for overlaps within the provided list
        if (sessionRequests.size() > 1) {
            List<SessionRequestDTO> sortedRequests = sessionRequests.stream()
                    .sorted(Comparator.comparing(SessionRequestDTO::startTime))
                    .toList();

            for (int i = 0; i < sortedRequests.size() - 1; i++) {
                SessionRequestDTO current = sortedRequests.get(i);
                SessionRequestDTO next = sortedRequests.get(i + 1);

                if (next.startTime().isBefore(current.endTime())) {
                    throw new IllegalArgumentException(String.format(
                            "Overlapping Session Data Exists: Session starting at %s overlaps with session ending at %s within the same request.",
                            next.startTime(), current.endTime()));
                }
            }
        }

        // 2. External Check: Check against existing sessions in DB
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

    private Session mapToEntity(Offering offering, SessionRequestDTO req) {
        return Session.builder()
                .offering(offering)
                .teacher(offering.getTeacher())
                // These are already normalized to UTC in the calling method
                .startTime(req.startTime())
                .endTime(req.endTime())
                .build();
    }

    private SessionResponseDTO mapToResponseDTO(Session session) {
        return SessionResponseDTO.builder()
                .id(session.getId())
                .offeringId(session.getOffering().getId())
                .teacherId(session.getTeacher().getId())
                .teacherName(session.getTeacher().getName())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .build();
    }
}