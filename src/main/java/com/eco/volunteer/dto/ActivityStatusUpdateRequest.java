package com.eco.volunteer.dto;

import com.eco.volunteer.model.ActivityStatus;

public class ActivityStatusUpdateRequest {
    private ActivityStatus status;
    private String reason;

    public ActivityStatus getStatus() {
        return status;
    }

    public void setStatus(ActivityStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
} 