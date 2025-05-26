package com.example.ontimeuvers.model;

import java.time.LocalDateTime;

public class InputManualRequest {

    private String nim;

    private String name;

    private LocalDateTime localDateTime;

    public InputManualRequest() {
    }

    public InputManualRequest(String nim, String name, LocalDateTime localDateTime) {
        this.nim = nim;
        this.name = name;
        this.localDateTime = localDateTime;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }
}
