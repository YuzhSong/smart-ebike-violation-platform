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

    long countByUser_Id(Long userId);

    ViolationEvent findTopByUser_IdOrderByEventTimeDesc(Long userId);

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

    @Query(value = """
            select date_format(event_time, '%Y-%m-%d') as day, count(*) as count
            from violation_event
            where event_time >= :start and event_time < :end
            group by date_format(event_time, '%Y-%m-%d')
            order by day
            """, nativeQuery = true)
    List<TrendCountProjection> countByDayBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(value = """
            select d.location_desc as name, count(*) as count
            from violation_event v
            join device_info d on v.device_id = d.id
            where v.event_time >= :start and v.event_time < :end
            group by d.location_desc
            order by count desc, d.location_desc asc
            limit :limit
            """, nativeQuery = true)
    List<LocationCountProjection> countByLocationBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("limit") int limit
    );

    interface TypeCountProjection {
        String getType();

        long getCount();
    }

    interface StatusCountProjection {
        String getStatus();

        long getCount();
    }

    interface TrendCountProjection {
        String getDay();

        long getCount();
    }

    interface LocationCountProjection {
        String getName();

        long getCount();
    }
}
