package com.example.ontimeuvers.service;


import android.util.Log;

import androidx.annotation.NonNull;

import com.example.ontimeuvers.entity.User;
import com.example.ontimeuvers.model.AdminResponse;
import com.example.ontimeuvers.model.LoginRequest;
import com.example.ontimeuvers.model.LoginResponse;
import com.example.ontimeuvers.model.UserResponse;
import com.example.ontimeuvers.repository.AdminRepository;
import com.example.ontimeuvers.repository.UserRepository;
import com.example.ontimeuvers.util.FirebaseUtil;
import com.google.firebase.database.DatabaseReference;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class AuthServiceImpl implements AuthService{

    private UserRepository userRepository;

    private AdminRepository adminRepository;

    private final DatabaseReference database = FirebaseUtil.getUsersReference();

    private final DatabaseReference databaseAdmin = FirebaseUtil.getAdminReference();

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthServiceImpl(UserRepository userRepository, AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public CompletableFuture<LoginResponse> login(@NonNull LoginRequest request){
        if (request.getNim().equals("admin") || request.getNim().equals("security") || request.getNim().equals("dba")){
            return adminRepository.findAdminByIdPassword(request.getNim(),request.getPassword()).thenApply(admin ->{
                admin.setToken(UUID.randomUUID().toString());
                admin.setTokenExpiredAt(next30Days());
                DatabaseReference adminDatabase = databaseAdmin.child(admin.getId());
                adminDatabase.child("token").setValue(admin.getToken());
                adminDatabase.child("tokenExpiredAt").setValue(admin.getTokenExpiredAt());
                Log.i("Auth", "Success login");

                return LoginResponse.builder()
                        .name(admin.getName())
                        .nim(admin.getId())
                        .token(admin.getToken())
                        .expiredAt(admin.getTokenExpiredAt())
                        .level(admin.getLevel())
                        .build();
            }).exceptionally(ex -> {
                Log.e("Auth", "Login gagal: " + ex.getMessage());
                throw new CompletionException(new RuntimeException("NIM atau password salah"));
            });
        }
        else {
            return userRepository.findUserByNimPassword(request.getNim(), request.getPassword()).thenApply(user -> {
                user.setToken(UUID.randomUUID().toString());
                user.setTokenExpiredAt(next30Days());
                DatabaseReference nim = database.child(user.getNim());
                nim.child("token").setValue(user.getToken());
                nim.child("tokenExpiredAt").setValue(user.getTokenExpiredAt());

                Log.i("Auth", "Success login");

                return LoginResponse.builder()
                        .name(user.getName())
                        .nim(user.getNim())
                        .jurusan(user.getJurusan().getNama())
                        .token(user.getToken())
                        .expiredAt(user.getTokenExpiredAt())
                        .level("mahasiswa")
                        .build();
            }).exceptionally(ex -> {
                Log.e("Auth", "Login gagal: " + ex.getMessage());
                throw new CompletionException(new RuntimeException("NIM atau password salah"));
            });
        }
    }

    @Override
    public CompletableFuture<AdminResponse> getAdminCurrent(String token) {
        Log.i("AuthService", token);
        return adminRepository.findAdminToken(token).thenApply(user ->
                new AdminResponse(user.getName(), user.getId(), user.getLevel())
        ).exceptionally(ex ->{
            Log.e("UserService", "User not found: " + ex.getMessage());
            throw new CompletionException(new RuntimeException("User Not found"));
        });
    }

    public Long next30Days(){
        return System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 30);
    }

    public CompletableFuture<Void> logoutAdmin(String token){

        return adminRepository.findAdminToken(token)
                .thenCompose(admin -> {
                    if (admin == null) {
                        Log.e("Auth", "User not found");
                        CompletableFuture<Void> failed = new CompletableFuture<>();
                        failed.completeExceptionally(new RuntimeException("User tidak ditemukan"));
                        return failed;
                    }
                    return adminRepository.deleteAdminToken(admin);
                });
    }

    public CompletableFuture<Void> logout(String token){

        return userRepository.findUserByToken(token)
                .thenCompose(user -> {
                    if (user == null) {
                        Log.e("Auth", "User not found");
                        CompletableFuture<Void> failed = new CompletableFuture<>();
                        failed.completeExceptionally(new RuntimeException("User tidak ditemukan"));
                        return failed;
                    }
                    return userRepository.deleteUserToken(user);
                });
    }


}


