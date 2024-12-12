package com.eco.volunteer.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eco.volunteer.dto.ActivityRegistrationRequest;
import com.eco.volunteer.model.Activity;
import com.eco.volunteer.model.ActivityRegistration;
import com.eco.volunteer.model.ActivityRegistrationStatus;
import com.eco.volunteer.model.ActivityStatus;
import com.eco.volunteer.model.Announcement;
import com.eco.volunteer.model.AnnouncementStatus;
import com.eco.volunteer.model.EcoKnowledge;
import com.eco.volunteer.model.User;
import com.eco.volunteer.repository.ActivityRegistrationRepository;
import com.eco.volunteer.repository.ActivityRepository;
import com.eco.volunteer.repository.AnnouncementRepository;
import com.eco.volunteer.repository.EcoKnowledgeRepository;
import com.eco.volunteer.repository.UserRepository;

@Service
public class VolunteerService {
    
    @Autowired
    private ActivityRepository activityRepository;
    
    @Autowired
    private ActivityRegistrationRepository registrationRepository;
    
    @Autowired
    private EcoKnowledgeRepository knowledgeRepository;
    
    @Autowired
    private AnnouncementRepository announcementRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public List<Activity> findPublicActivities() {
        // 查找已通过审核且未结束的活动
        List<Activity> activities = activityRepository.findByStatusIn(
            Arrays.asList(ActivityStatus.APPROVED, ActivityStatus.ONGOING)
        );
        
        // 检查活动是否已开始，如果已开始则更新状态为进行中
        LocalDateTime now = LocalDateTime.now();
        activities.forEach(activity -> {
            if (activity.getStatus() == ActivityStatus.APPROVED 
                && activity.getStartTime().isBefore(now)) {
                activity.setStatus(ActivityStatus.ONGOING);
                activityRepository.save(activity);
            }
        });
        
        // 过滤掉已结束的活动
        return activities.stream()
            .filter(activity -> activity.getEndTime().isAfter(now))
            .collect(Collectors.toList());
    }
    
    public List<Activity> findVolunteerActivities(Long volunteerId) {
        return registrationRepository.findByVolunteerId(volunteerId)
            .stream()
            .map(ActivityRegistration::getActivity)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public ActivityRegistration registerActivity(ActivityRegistrationRequest request) {
        Activity activity = activityRepository.findById(request.getActivityId())
            .orElseThrow(() -> new RuntimeException("活动不存在"));
            
        if (activity.getStatus() != ActivityStatus.APPROVED) {
            throw new RuntimeException("活动未通过审核或已结束");
        }
        
        if (activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            throw new RuntimeException("活动已满员");
        }
        
        if (registrationRepository.existsByActivityIdAndVolunteerId(
                request.getActivityId(), request.getVolunteerId())) {
            throw new RuntimeException("您已报名此活动");
        }
        
        ActivityRegistration registration = new ActivityRegistration();
        registration.setActivity(activity);
        registration.setVolunteerId(request.getVolunteerId());
        registration.setStatus(ActivityRegistrationStatus.PENDING);
        
        activity.setCurrentParticipants(activity.getCurrentParticipants() + 1);
        activityRepository.save(activity);
        
        return registrationRepository.save(registration);
    }
    
    @Transactional
    public void cancelRegistration(Long activityId) {
        List<ActivityRegistration> registrations = registrationRepository.findByActivityId(activityId);
        if (registrations.isEmpty()) {
            throw new RuntimeException("报名记录不存在");
        }
        ActivityRegistration registration = registrations.get(0);
            
        if (registration.getStatus() != ActivityRegistrationStatus.PENDING) {
            throw new RuntimeException("当前状态无法取消报名");
        }
        
        Activity activity = registration.getActivity();
        activity.setCurrentParticipants(activity.getCurrentParticipants() - 1);
        activityRepository.save(activity);
        
        registrationRepository.delete(registration);
    }
    
    public List<EcoKnowledge> findAllKnowledge() {
        List<EcoKnowledge> knowledge = knowledgeRepository.findAll();
        knowledge.forEach(k -> {
            if (k.getAuthor() == null) {
                User admin = userRepository.findByUsername("admin")
                    .orElseThrow(() -> new RuntimeException("管理员用户不存在"));
                k.setAuthor(admin);
            }
        });
        return knowledge;
    }
    
    @Transactional(readOnly = true)
    public List<Announcement> findPublishedAnnouncements() {
        List<Announcement> announcements = announcementRepository.findByStatus(AnnouncementStatus.PUBLISHED);
        announcements.forEach(a -> {
            if (a.getAuthor() == null) {
                User admin = userRepository.findByUsername("admin")
                    .orElseThrow(() -> new RuntimeException("管理员用户不存在"));
                a.setAuthor(admin);
            }
            if (a.getAuthor() != null) {
                a.getAuthor().setPassword(null);
            }
        });
        return announcements;
    }
    
    @Transactional(readOnly = true)
    public boolean hasAnnouncementUpdates(LocalDateTime lastCheckTime) {
        return announcementRepository.hasUpdates(AnnouncementStatus.PUBLISHED, lastCheckTime);
    }
} 