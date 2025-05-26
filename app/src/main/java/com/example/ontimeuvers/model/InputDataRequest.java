package com.example.ontimeuvers.model;

import com.example.ontimeuvers.entity.User;

import java.time.LocalDateTime;

public class InputDataRequest {

    private LocalDateTime localDateTime;

    public InputDataRequest() {
    }

    public InputDataRequest(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

}
