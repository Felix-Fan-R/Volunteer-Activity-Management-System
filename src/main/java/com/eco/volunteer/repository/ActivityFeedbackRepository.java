package com.eco.volunteer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eco.volunteer.model.Activity;
import com.eco.volunteer.model.ActivityFeedback;
import com.eco.volunteer.model.User;

@Repository
public interface ActivityFeedbackRepository extends JpaRepository<ActivityFeedback, Long> {
    List<ActivityFeedback> findByActivity(Activity activity);
    List<ActivityFeedback> findByVolunteer(User volunteer);
    boolean existsByActivityAndVolunteer(Activity activity, User volunteer);
} 