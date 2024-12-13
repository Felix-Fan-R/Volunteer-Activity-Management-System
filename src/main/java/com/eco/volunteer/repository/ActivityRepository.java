package com.eco.volunteer.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eco.volunteer.model.Activity;
import com.eco.volunteer.model.ActivityStatus;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByOrganizerId(Long organizerId);

    int countByOrganizerId(Long organizerId);
    List<Activity> findByStatusIn(Collection<ActivityStatus> statuses);
    
    @Query("SELECT a FROM Activity a WHERE a.status IN :statuses AND a.endTime > :now")
    List<Activity> findAvailableActivities(
        @Param("statuses") Collection<ActivityStatus> statuses, 
        @Param("now") LocalDateTime now
    );

    @Query("SELECT a FROM Activity a WHERE a.status = :status ORDER BY a.createTime DESC")
    List<Activity> findByStatus(@Param("status") ActivityStatus status);
} 