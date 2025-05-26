package com.example.ontimeuvers.service;

import com.example.ontimeuvers.model.DetailUserResponse;
import com.example.ontimeuvers.model.InputDataRequest;
import com.example.ontimeuvers.model.InputDataResponse;
import com.example.ontimeuvers.model.LoginRequest;
import com.example.ontimeuvers.model.LoginResponse;
import com.example.ontimeuvers.model.UpdatePasswordRequest;
import com.example.ontimeuvers.model.UserResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UserService {

    CompletableFuture<UserResponse> getCurrent(String token);

    CompletableFuture<List<DetailUserResponse>> getAllDataUserCurrent(String token);

    CompletableFuture<InputDataResponse> inputDataKeterlambatan(InputDataRequest request, String user);

    CompletableFuture<String> updatePassword(UpdatePasswordRequest request);

}
