package com.eco.volunteer.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eco.volunteer.dto.ActivityRequest;
import com.eco.volunteer.model.Activity;
import com.eco.volunteer.model.ActivityFeedback;
import com.eco.volunteer.model.ActivityRegistration;
import com.eco.volunteer.model.ActivityRegistrationStatus;
import com.eco.volunteer.model.ActivityStatus;
import com.eco.volunteer.model.Announcement;
import com.eco.volunteer.repository.ActivityRepository;
import com.eco.volunteer.service.OrganizerService;

@RestController
@RequestMapping("/api/organizer")
public class OrganizerController {
    
    @Autowired
    private OrganizerService organizerService;
    
    @Autowired
    private ActivityRepository activityRepository;
    
    @GetMapping("/{organizerId}/activities")
    public ResponseEntity<List<Activity>> getOrganizerActivities(@PathVariable Long organizerId) {
        List<Activity> activities = organizerService.findOrganizerActivities(organizerId);
        // 检查活动状态并更新
        LocalDateTime now = LocalDateTime.now();
        activities.forEach(activity -> {
            if (activity.getStatus() == ActivityStatus.APPROVED && 
                activity.getStartTime().isBefore(now)) {
                activity.setStatus(ActivityStatus.ONGOING);
            } else if (activity.getEndTime().isBefore(now)) {
                activity.setStatus(ActivityStatus.FINISHED);
            }
            activityRepository.save(activity);
        });
        return ResponseEntity.ok(activities);
    }
    
    @GetMapping("/activities/{id}")
    public ResponseEntity<Activity> getActivity(@PathVariable Long id) {
        return ResponseEntity.ok(organizerService.findActivityById(id));
    }
    
    @PostMapping("/activities")
    public ResponseEntity<Activity> createActivity(@RequestBody ActivityRequest request) {
        try {
            return ResponseEntity.ok(organizerService.createActivity(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @PutMapping("/activities/{id}")
    public ResponseEntity<Activity> updateActivity(
            @PathVariable Long id,
            @RequestBody ActivityRequest request) {
        try {
            request.setId(id);
            return ResponseEntity.ok(organizerService.updateActivity(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @PutMapping("/activities/{id}/submit")
    public ResponseEntity<?> submitActivity(@PathVariable Long id) {
        try {
            Activity activity = organizerService.submitActivity(id);
            return ResponseEntity.ok(new ApiResponse("提交成功", activity));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }
    
    @GetMapping("/activities/{activityId}/registrations")
    public ResponseEntity<List<ActivityRegistration>> getActivityRegistrations(@PathVariable Long activityId) {
        return ResponseEntity.ok(organizerService.findActivityRegistrations(activityId));
    }
    
    @GetMapping("/activities/{activityId}/feedback")
    public ResponseEntity<List<ActivityFeedback>> getActivityFeedback(@PathVariable Long activityId) {
        return ResponseEntity.ok(organizerService.findActivityFeedback(activityId));
    }
    
    @PutMapping("/registrations/{registrationId}/status")
    public ResponseEntity<ActivityRegistration> updateRegistrationStatus(
            @PathVariable Long registrationId,
            @RequestBody ActivityRegistrationStatus status) {
        return ResponseEntity.ok(organizerService.updateRegistrationStatus(registrationId, status));
    }
    
    @GetMapping("/announcements")
    public ResponseEntity<List<Announcement>> getAnnouncements() {
        return ResponseEntity.ok(organizerService.findPublishedAnnouncements());
    }
    
    @GetMapping("/announcements/check-updates")
    public ResponseEntity<Boolean> checkAnnouncementUpdates(@RequestParam LocalDateTime lastCheckTime) {
        return ResponseEntity.ok(organizerService.hasAnnouncementUpdates(lastCheckTime));
    }
}

class ApiResponse {
    private String message;
    private Object data;

    public ApiResponse(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
} 