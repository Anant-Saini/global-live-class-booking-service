package com.undoschool.platform.globalliveclassbookingservice.repository;

import com.undoschool.platform.globalliveclassbookingservice.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    @Query("SELECT COUNT(s) > 0 FROM Session s " +
           "WHERE s.teacher.id = :teacherId " +
           "AND (:startTime < s.endTime AND :endTime > s.startTime)")
    boolean existsTeacherOverlap(@Param("teacherId") Long teacherId, 
                                 @Param("startTime") ZonedDateTime startTime, 
                                 @Param("endTime") ZonedDateTime endTime);

    List<Session> findByOfferingId(Long offeringId);

    // Efficiently finds all sessions for multiple offerings (used to prevent N+1 queries)
    List<Session> findByOfferingIdIn(List<Long> offeringIds);
}