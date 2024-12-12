package com.eco.volunteer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eco.volunteer.dto.ActivityRegistrationRequest;
import com.eco.volunteer.model.Activity;
import com.eco.volunteer.model.Announcement;
import com.eco.volunteer.model.EcoKnowledge;
import com.eco.volunteer.service.VolunteerService;

@RestController
@RequestMapping("/api/volunteer")
public class VolunteerController {
    
    @Autowired
    private VolunteerService volunteerService;
    
    @GetMapping("/activities")
    public ResponseEntity<List<Activity>> getPublicActivities() {
        return ResponseEntity.ok(volunteerService.findPublicActivities());
    }
    
    @GetMapping("/{volunteerId}/activities")
    public ResponseEntity<List<Activity>> getVolunteerActivities(@PathVariable Long volunteerId) {
        return ResponseEntity.ok(volunteerService.findVolunteerActivities(volunteerId));
    }
    
    @PostMapping("/activities/register")
    public ResponseEntity<?> registerActivity(@RequestBody ActivityRegistrationRequest request) {
        return ResponseEntity.ok(volunteerService.registerActivity(request));
    }
    
    @DeleteMapping("/activities/register/{activityId}")
    public ResponseEntity<?> cancelRegistration(@PathVariable Long activityId) {
        volunteerService.cancelRegistration(activityId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/knowledge")
    public ResponseEntity<List<EcoKnowledge>> getKnowledge() {
        return ResponseEntity.ok(volunteerService.findAllKnowledge());
    }
    
    @GetMapping("/announcements")
    public ResponseEntity<List<Announcement>> getAnnouncements() {
        return ResponseEntity.ok(volunteerService.findPublishedAnnouncements());
    }
} 