package com.example.ontimeuvers.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginRequest{

    private String nim;

    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String nim, String password) {
        this.nim = nim;
        this.password = password;
    }
}
