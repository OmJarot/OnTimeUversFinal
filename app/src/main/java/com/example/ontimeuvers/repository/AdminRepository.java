package com.example.ontimeuvers.repository;

import com.example.ontimeuvers.entity.Admin;
import com.example.ontimeuvers.entity.User;

import java.util.concurrent.CompletableFuture;

public interface AdminRepository {

    CompletableFuture<Admin> findAdminByIdPassword(String id, String password);

    CompletableFuture<Admin> findAdminToken(String token);

    CompletableFuture<Void> deleteAdminToken(Admin admin);

    CompletableFuture<Admin> updateAdmin(Admin admin);

}
