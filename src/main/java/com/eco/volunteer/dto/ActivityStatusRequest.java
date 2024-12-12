package com.eco.volunteer.dto;

import com.eco.volunteer.model.ActivityStatus;

public class ActivityStatusRequest {
    private ActivityStatus status;

    public ActivityStatus getStatus() {
        return status;
    }

    public void setStatus(ActivityStatus status) {
        this.status = status;
    }
} 