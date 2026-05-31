package com.undoschool.platform.globalliveclassbookingservice.repository;

import com.undoschool.platform.globalliveclassbookingservice.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    boolean existsByRegistrant_IdAndOffering_Id(Long registrantId, Long offeringId);

    List<Booking> findByRegistrant_Id(Long registrantId);

    @Query("SELECT COUNT(s_existing) > 0 " +
           "FROM Session s_existing " +
           "JOIN Booking b ON s_existing.offering.id = b.offering.id " +
           "WHERE b.registrant.id = :parentId " +
           "AND EXISTS (" +
           "  SELECT 1 FROM Session s_new " +
           "  WHERE s_new.offering.id = :targetOfferingId " +
           "  AND (s_new.startTime < s_existing.endTime AND s_new.endTime > s_existing.startTime)" +
           ")")
    boolean hasScheduleConflict(
            @Param("parentId") Long parentId, 
            @Param("targetOfferingId") Long targetOfferingId);
}