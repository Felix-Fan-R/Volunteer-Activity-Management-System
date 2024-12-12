package com.eco.volunteer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eco.volunteer.dto.ActivityStatusUpdateRequest;
import com.eco.volunteer.dto.AnnouncementDTO;
import com.eco.volunteer.dto.EcoKnowledgeDTO;
import com.eco.volunteer.dto.StatusUpdateRequest;
import com.eco.volunteer.model.Activity;
import com.eco.volunteer.service.AdminService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @GetMapping("/organizers")
    public ResponseEntity<?> getOrganizers() {
        return ResponseEntity.ok(adminService.findAllOrganizers());
    }
    
    @PutMapping("/organizers/{id}/status")
    public ResponseEntity<?> updateOrganizerStatus(
            @PathVariable Long id, 
            @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(adminService.updateOrganizerStatus(id, request.getStatus()));
    }
    
    @GetMapping("/organizers/{id}/activities")
    public ResponseEntity<?> getOrganizerActivities(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.findOrganizerActivities(id));
    }
    
    @GetMapping("/volunteers")
    public ResponseEntity<?> getVolunteers() {
        return ResponseEntity.ok(adminService.findAllVolunteers());
    }
    
    @PutMapping("/volunteers/{id}/status")
    public ResponseEntity<?> updateVolunteerStatus(
            @PathVariable Long id, 
            @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(adminService.updateVolunteerStatus(id, request.getStatus()));
    }
    
    @GetMapping("/volunteers/{id}/activities")
    public ResponseEntity<?> getVolunteerActivities(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.findVolunteerActivities(id));
    }
    
    @GetMapping("/activities")
    public ResponseEntity<List<Activity>> getActivities(
            @RequestParam(required = false) String status) {
        try {
            List<Activity> activities = adminService.findActivitiesByStatus(status);
            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @PutMapping("/activities/{id}/status")
    public ResponseEntity<?> updateActivityStatus(
            @PathVariable Long id,
            @RequestBody ActivityStatusUpdateRequest request) {
        try {
            Activity activity = adminService.updateActivityStatus(id, request.getStatus(), request.getReason());
            return ResponseEntity.ok(new ApiResponse("操作成功", activity));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }
    
    @GetMapping("/knowledge")
    public ResponseEntity<List<EcoKnowledgeDTO>> getKnowledge() {
        return ResponseEntity.ok(adminService.findAllKnowledge());
    }
    
    @GetMapping("/knowledge/{id}")
    public ResponseEntity<?> getKnowledgeById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.findKnowledgeById(id));
    }
    
    @PostMapping("/knowledge")
    public ResponseEntity<?> createKnowledge(@RequestBody EcoKnowledgeDTO request) {
        return ResponseEntity.ok(adminService.createKnowledge(request));
    }
    
    @PutMapping("/knowledge/{id}")
    public ResponseEntity<?> updateKnowledge(
            @PathVariable Long id,
            @RequestBody EcoKnowledgeDTO request) {
        return ResponseEntity.ok(adminService.updateKnowledge(id, request));
    }
    
    @DeleteMapping("/knowledge/{id}")
    public ResponseEntity<?> deleteKnowledge(@PathVariable Long id) {
        adminService.deleteKnowledge(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/announcements")
    public ResponseEntity<?> getAnnouncements() {
        return ResponseEntity.ok(adminService.findAllAnnouncements());
    }
    
    @GetMapping("/announcements/{id}")
    public ResponseEntity<?> getAnnouncementById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.findAnnouncementById(id));
    }
    
    @PostMapping("/announcements")
    public ResponseEntity<?> createAnnouncement(@RequestBody AnnouncementDTO request) {
        return ResponseEntity.ok(adminService.createAnnouncement(request));
    }
    
    @PutMapping("/announcements/{id}")
    public ResponseEntity<?> updateAnnouncement(
            @PathVariable Long id,
            @RequestBody AnnouncementDTO request) {
        return ResponseEntity.ok(adminService.updateAnnouncement(id, request));
    }
    
    @DeleteMapping("/announcements/{id}")
    public ResponseEntity<?> deleteAnnouncement(@PathVariable Long id) {
        adminService.deleteAnnouncement(id);
        return ResponseEntity.ok().build();
    }
} 