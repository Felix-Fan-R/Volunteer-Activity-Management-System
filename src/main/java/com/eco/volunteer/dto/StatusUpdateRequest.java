package com.eco.volunteer.dto;

import com.eco.volunteer.model.Status;

public class StatusUpdateRequest {
    private Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
} 