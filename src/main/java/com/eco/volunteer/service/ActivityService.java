package com.eco.volunteer.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eco.volunteer.dto.ActivityRegistrationRequest;
import com.eco.volunteer.dto.ActivityRequest;
import com.eco.volunteer.model.Activity;
import com.eco.volunteer.model.ActivityStatus;
import com.eco.volunteer.model.User;
import com.eco.volunteer.repository.ActivityRepository;
import com.eco.volunteer.repository.UserRepository;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Activity> findPublicActivities() {
        return activityRepository.findByStatus(ActivityStatus.APPROVED);
    }

    public List<Activity> findPendingActivities() {
        return activityRepository.findByStatus(ActivityStatus.DRAFT);
    }

    public List<Activity> findByOrganizerId(Long organizerId) {
        return activityRepository.findByOrganizerId(organizerId);
    }

    @Transactional
    public Activity createActivity(ActivityRequest request) {
        User organizer = userRepository.findById(request.getOrganizerId())
            .orElseThrow(() -> new RuntimeException("组织者不存在"));

        Activity activity = new Activity();
        activity.setTitle(request.getTitle());
        activity.setDescription(request.getDescription());
        activity.setLocation(request.getLocation());
        activity.setStartTime(request.getStartTime());
        activity.setEndTime(request.getEndTime());
        activity.setMaxParticipants(request.getMaxParticipants());
        activity.setCurrentParticipants(0);
        activity.setOrganizer(organizer);
        activity.setStatus(ActivityStatus.DRAFT);

        return activityRepository.save(activity);
    }

    @Transactional
    public Activity registerActivity(ActivityRegistrationRequest request) {
        Activity activity = activityRepository.findById(request.getActivityId())
            .orElseThrow(() -> new RuntimeException("活动不存在"));

        if (activity.getStatus() != ActivityStatus.APPROVED) {
            throw new RuntimeException("活动未通过审核或已结束");
        }

        if (activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            throw new RuntimeException("活动已满员");
        }

        activity.setCurrentParticipants(activity.getCurrentParticipants() + 1);
        return activityRepository.save(activity);
    }

    @Transactional
    public Activity updateStatus(Long id, ActivityStatus status) {
        Activity activity = activityRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("活动不存在"));

        activity.setStatus(status);
        return activityRepository.save(activity);
    }
} 