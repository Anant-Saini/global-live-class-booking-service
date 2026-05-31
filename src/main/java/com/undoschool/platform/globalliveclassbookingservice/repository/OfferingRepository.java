package com.undoschool.platform.globalliveclassbookingservice.repository;

import com.undoschool.platform.globalliveclassbookingservice.entity.Offering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface OfferingRepository extends JpaRepository<Offering, Long> {
    List<Offering> findByTeacherId(Long teacherId);

    // "JOIN FETCH" tells Hibernate to grab the Course and Teacher data 
    // in the same SELECT statement to avoid the N+1 problem.
    @Query("SELECT o FROM Offering o JOIN FETCH o.course JOIN FETCH o.teacher WHERE o.teacher.id = :teacherId")
    List<Offering> findByTeacherIdWithDetails(@Param("teacherId") Long teacherId);


    @Query("SELECT o FROM Offering o " +
            "JOIN Session s ON s.offering = o " +
            "WHERE o.teacher.id = :teacherId " +
            "AND s.endTime > :now " +
            "GROUP BY o.id " +
            "ORDER BY MIN(s.startTime) ASC")
    List<Offering> findUpcomingByTeacher(@Param("teacherId") Long teacherId, @Param("now") ZonedDateTime now);
}
