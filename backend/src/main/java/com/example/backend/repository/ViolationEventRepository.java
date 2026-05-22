package com.example.backend.repository;

import com.example.backend.entity.ViolationEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ViolationEventRepository extends JpaRepository<ViolationEvent, Long>, JpaSpecificationExecutor<ViolationEvent> {
    List<ViolationEvent> findByUser_IdOrderByEventTimeDesc(Long userId);

    long countByEventTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    @Query("""
            select v.violationType as type, count(v) as count
            from ViolationEvent v
            where v.eventTime between :start and :end
            group by v.violationType
            """)
    List<TypeCountProjection> countByTypeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
            select v.status as status, count(v) as count
            from ViolationEvent v
            where v.eventTime between :start and :end
            group by v.status
            """)
    List<StatusCountProjection> countByStatusBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    interface TypeCountProjection {
        String getType();

        long getCount();
    }

    interface StatusCountProjection {
        String getStatus();

        long getCount();
    }
}
