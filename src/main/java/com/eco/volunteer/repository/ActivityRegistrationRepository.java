package com.eco.volunteer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eco.volunteer.model.ActivityRegistration;
import com.eco.volunteer.model.ActivityRegistrationStatus;

@Repository
public interface ActivityRegistrationRepository extends JpaRepository<ActivityRegistration, Long> {
    List<ActivityRegistration> findByVolunteerId(Long volunteerId);
    List<ActivityRegistration> findByActivityId(Long activityId);
    boolean existsByActivityIdAndVolunteerId(Long activityId, Long volunteerId);
    List<ActivityRegistration> findByActivityIdAndStatus(Long activityId, ActivityRegistrationStatus status);
} 