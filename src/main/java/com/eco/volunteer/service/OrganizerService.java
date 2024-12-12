package com.eco.volunteer.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eco.volunteer.dto.ActivityRequest;
import com.eco.volunteer.model.Activity;
import com.eco.volunteer.model.ActivityFeedback;
import com.eco.volunteer.model.ActivityRegistration;
import com.eco.volunteer.model.ActivityRegistrationStatus;
import com.eco.volunteer.model.ActivityStatus;
import com.eco.volunteer.model.Announcement;
import com.eco.volunteer.model.AnnouncementStatus;
import com.eco.volunteer.model.Role;
import com.eco.volunteer.model.User;
import com.eco.volunteer.repository.ActivityFeedbackRepository;
import com.eco.volunteer.repository.ActivityRegistrationRepository;
import com.eco.volunteer.repository.ActivityRepository;
import com.eco.volunteer.repository.AnnouncementRepository;
import com.eco.volunteer.repository.UserRepository;

@Service
public class OrganizerService {
    
    @Autowired
    private ActivityRepository activityRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ActivityRegistrationRepository registrationRepository;
    
    @Autowired
    private ActivityFeedbackRepository feedbackRepository;
    
    @Autowired
    private AnnouncementRepository announcementRepository;
    
    @Transactional(readOnly = true)
    public List<Activity> findOrganizerActivities(Long organizerId) {
        return activityRepository.findByOrganizerId(organizerId);
    }
    
    @Transactional(readOnly = true)
    public Activity findActivityById(Long id) {
        return activityRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("活动不存在"));
    }
    
    @Transactional
    public Activity createActivity(ActivityRequest request) {
        try {
            User organizer = userRepository.findById(request.getOrganizerId())
                .orElseThrow(() -> new RuntimeException("组织者不存在"));
            
            if (organizer.getRole() != Role.ORGANIZER) {
                throw new RuntimeException("该用户不是组织者");
            }
            
            if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
                throw new RuntimeException("活动标题不能为空");
            }
            if (request.getLocation() == null || request.getLocation().trim().isEmpty()) {
                throw new RuntimeException("活动地点不能为空");
            }
            if (request.getStartTime() == null) {
                throw new RuntimeException("开始时间不能为空");
            }
            if (request.getEndTime() == null) {
                throw new RuntimeException("结束时间不能为空");
            }
            if (request.getEndTime().isBefore(request.getStartTime())) {
                throw new RuntimeException("结束时间必须晚于开始时间");
            }
            if (request.getMaxParticipants() == null || request.getMaxParticipants() < 1) {
                throw new RuntimeException("最大参与人数必须大于0");
            }
            
            Activity activity = new Activity();
            activity.setTitle(request.getTitle().trim());
            activity.setDescription(request.getDescription() != null ? request.getDescription().trim() : "");
            activity.setLocation(request.getLocation().trim());
            activity.setStartTime(request.getStartTime());
            activity.setEndTime(request.getEndTime());
            activity.setMaxParticipants(request.getMaxParticipants());
            activity.setCurrentParticipants(0);
            activity.setOrganizer(organizer);
            activity.setStatus(ActivityStatus.DRAFT);
            activity.setCreateTime(LocalDateTime.now());
            activity.setUpdateTime(LocalDateTime.now());
            
            return activityRepository.save(activity);
        } catch (Exception e) {
            throw new RuntimeException("创建活动失败：" + e.getMessage());
        }
    }
    
    @Transactional
    public Activity updateActivity(ActivityRequest request) {
        Activity activity = activityRepository.findById(request.getId())
            .orElseThrow(() -> new RuntimeException("活动不存在"));
            
        if (!activity.getOrganizer().getId().equals(request.getOrganizerId())) {
            throw new RuntimeException("无权修改此活动");
        }
        
        if (activity.getStatus() != ActivityStatus.DRAFT) {
            throw new RuntimeException("只能修改草稿状态的活动");
        }
        
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new RuntimeException("活动标题不能为空");
        }
        if (request.getLocation() == null || request.getLocation().trim().isEmpty()) {
            throw new RuntimeException("活动地点不能为空");
        }
        if (request.getStartTime() == null) {
            throw new RuntimeException("开始时间不能为空");
        }
        if (request.getEndTime() == null) {
            throw new RuntimeException("结束时间不能为空");
        }
        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new RuntimeException("结束时间必须晚于开始时间");
        }
        if (request.getMaxParticipants() == null || request.getMaxParticipants() < 1) {
            throw new RuntimeException("最大参与人数必须大于0");
        }
        
        activity.setTitle(request.getTitle().trim());
        activity.setDescription(request.getDescription() != null ? request.getDescription().trim() : "");
        activity.setLocation(request.getLocation().trim());
        activity.setStartTime(request.getStartTime());
        activity.setEndTime(request.getEndTime());
        activity.setMaxParticipants(request.getMaxParticipants());
        activity.setUpdateTime(LocalDateTime.now());
        
        return activityRepository.save(activity);
    }
    
    @Transactional
    public ActivityRegistration updateRegistrationStatus(Long registrationId, ActivityRegistrationStatus status) {
        ActivityRegistration registration = registrationRepository.findById(registrationId)
            .orElseThrow(() -> new RuntimeException("报名记录不存在"));
            
        if (!registration.getActivity().getOrganizer().getId().equals(registration.getActivity().getOrganizer().getId())) {
            throw new RuntimeException("无权操作此报名记录");
        }
        
        if (registration.getStatus() != ActivityRegistrationStatus.PENDING) {
            throw new RuntimeException("只能处理待审核的报名");
        }
        
        registration.setStatus(status);
        registration.setUpdateTime(LocalDateTime.now());
        
        if (status == ActivityRegistrationStatus.REJECTED) {
            Activity activity = registration.getActivity();
            activity.setCurrentParticipants(activity.getCurrentParticipants() - 1);
            activityRepository.save(activity);
        }
        
        return registrationRepository.save(registration);
    }
    
    public List<ActivityRegistration> findActivityRegistrations(Long activityId) {
        Activity activity = activityRepository.findById(activityId)
            .orElseThrow(() -> new RuntimeException("活动不存在"));
            
        if (!activity.getOrganizer().getId().equals(activity.getOrganizer().getId())) {
            throw new RuntimeException("无权查看此活动的报名记录");
        }
        
        return registrationRepository.findByActivityId(activityId);
    }
    
    public List<ActivityFeedback> findActivityFeedback(Long activityId) {
        Activity activity = activityRepository.findById(activityId)
            .orElseThrow(() -> new RuntimeException("活动不存在"));
            
        if (!activity.getOrganizer().getId().equals(activity.getOrganizer().getId())) {
            throw new RuntimeException("无权查看此活动的评价");
        }
        
        return feedbackRepository.findByActivity(activity);
    }
    
    @Transactional
    public Activity submitActivity(Long id) {
        try {
            Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("活动不存在"));
            
            if (!activity.getOrganizer().getId().equals(activity.getOrganizer().getId())) {
                throw new RuntimeException("无权提交此活动");
            }
            
            if (activity.getStatus() != ActivityStatus.DRAFT) {
                throw new RuntimeException("只能提交草稿状态的活动");
            }
            
            if (activity.getTitle() == null || activity.getTitle().trim().isEmpty()) {
                throw new RuntimeException("活动标题不能为空");
            }
            if (activity.getDescription() == null || activity.getDescription().trim().isEmpty()) {
                throw new RuntimeException("活动描述不能为空");
            }
            if (activity.getLocation() == null || activity.getLocation().trim().isEmpty()) {
                throw new RuntimeException("活动地点不能为空");
            }
            if (activity.getStartTime() == null) {
                throw new RuntimeException("开始时间不能为空");
            }
            if (activity.getEndTime() == null) {
                throw new RuntimeException("结束时间不能为空");
            }
            if (activity.getEndTime().isBefore(activity.getStartTime())) {
                throw new RuntimeException("结束时间必须晚于开始时间");
            }
            if (activity.getMaxParticipants() == null || activity.getMaxParticipants() < 1) {
                throw new RuntimeException("最大参与人数必须大于0");
            }
            
            activity.setStatus(ActivityStatus.PENDING);
            activity.setUpdateTime(LocalDateTime.now());
            
            return activityRepository.save(activity);
        } catch (Exception e) {
            throw new RuntimeException("提交审核失败：" + e.getMessage());
        }
    }
    
    @Transactional(readOnly = true)
    public List<Announcement> findPublishedAnnouncements() {
        List<Announcement> announcements = announcementRepository.findByStatus(AnnouncementStatus.PUBLISHED);
        announcements.forEach(a -> {
            if (a.getAuthor() != null) {
                a.getAuthor().setPassword(null);  // 安全考虑，不返回密码
            }
        });
        return announcements;
    }

    @Transactional(readOnly = true)
    public boolean hasAnnouncementUpdates(LocalDateTime lastCheckTime) {
        return announcementRepository.hasUpdates(AnnouncementStatus.PUBLISHED, lastCheckTime);
    }
} 