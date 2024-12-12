package com.eco.volunteer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.eco.volunteer.dto.ActivityRequest;
import com.eco.volunteer.dto.ActivityRegistrationRequest;
import com.eco.volunteer.dto.ActivityStatusRequest;
import com.eco.volunteer.service.ActivityService;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @GetMapping("/public")
    public ResponseEntity<?> getPublicActivities() {
        return ResponseEntity.ok(activityService.findPublicActivities());
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingActivities() {
        return ResponseEntity.ok(activityService.findPendingActivities());
    }

    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<?> getOrganizerActivities(@PathVariable Long organizerId) {
        return ResponseEntity.ok(activityService.findByOrganizerId(organizerId));
    }

    @PostMapping
    public ResponseEntity<?> createActivity(@RequestBody ActivityRequest request) {
        return ResponseEntity.ok(activityService.createActivity(request));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerActivity(@RequestBody ActivityRegistrationRequest request) {
        return ResponseEntity.ok(activityService.registerActivity(request));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody ActivityStatusRequest request) {
        return ResponseEntity.ok(activityService.updateStatus(id, request.getStatus()));
    }
} 