package com.example.ontimeuvers.service;

import com.example.ontimeuvers.model.AdminResponse;
import com.example.ontimeuvers.model.LoginRequest;
import com.example.ontimeuvers.model.LoginResponse;

import java.util.concurrent.CompletableFuture;

public interface AuthService {

    CompletableFuture<LoginResponse> login(LoginRequest request);

    CompletableFuture<AdminResponse> getAdminCurrent(String token);

    CompletableFuture<Void> logoutAdmin(String token);

    CompletableFuture<Void> logout(String token);

}
