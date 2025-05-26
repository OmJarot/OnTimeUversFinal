package com.example.ontimeuvers.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.ontimeuvers.entity.Admin;
import com.example.ontimeuvers.entity.User;
import com.example.ontimeuvers.util.FirebaseUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

public class AdminRepositoryImpl implements AdminRepository{

    private final DatabaseReference database = FirebaseUtil.getAdminReference();
    @Override
    public CompletableFuture<Admin> findAdminByIdPassword(String id, String password) {
        CompletableFuture<Admin> future = new CompletableFuture<>();

        DatabaseReference userId = database.child(id);

        userId.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    Log.e("AdminRepository", "User not found");
                    future.completeExceptionally(new Exception("User not found"));
                    return;
                }
                Admin admin = snapshot.getValue(Admin.class);

                if (admin != null && admin.getPassword().equals(password)){
                    Log.i("AdminRepository", "Found admin: "+ admin.getName());
                    future.complete(admin);
                }else {
                    future.complete(null);
                    Log.e("AdminRepository", "Admin not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.completeExceptionally(new Exception(error.getMessage()));
                Log.e("Firebase", "Error: " + error.getMessage());
            }
        });
        return future;
    }

    @Override
    public CompletableFuture<Admin> findAdminToken(String token) {
        CompletableFuture<Admin> future = new CompletableFuture<>();

        Query query = database.orderByChild("token").equalTo(token);//database sudah ref admin

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Log.e("AdminRepository", "Admin not found");
                    future.completeExceptionally(new Exception("Admin not Found"));
                    return;
                }
                Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();
                if (!iterator.hasNext()) {
                    future.completeExceptionally(new Exception("Admin not found"));
                    return;
                }
                DataSnapshot data = snapshot.getChildren().iterator().next();

                Admin admin = data.getValue(Admin.class);
                if (admin == null || admin.getTokenExpiredAt() < System.currentTimeMillis()) {
                    Log.e("AdminRepository", "Token Expired");
                    future.completeExceptionally(new Exception("Token Expired"));
                    return;
                }
                future.complete(admin);
                Log.i("AdminRepository","Success get admin: "+ admin.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.completeExceptionally(new Exception(error.getMessage()));
                Log.e("Firebase", "Error: " + error.getMessage());
            }
        });
        return future;
    }

    @Override
    public CompletableFuture<Void> deleteAdminToken(Admin admin) {
        return CompletableFuture.supplyAsync(() -> {
            DatabaseReference id = database.child(admin.getId());
            CountDownLatch latch = new CountDownLatch(2);
            final Exception[] exceptionHolder = {null};

            id.child("token").removeValue()
                    .addOnSuccessListener(success -> {
                        Log.i("Auth", "Success delete token from database");
                        latch.countDown();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Auth", "Failed delete token from database");
                        exceptionHolder[0] = e;
                        latch.countDown();
                    });

            id.child("tokenExpiredAt").removeValue()
                    .addOnSuccessListener(success -> {
                        Log.i("Auth", "Success delete tokenExpired from database");
                        latch.countDown();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Auth", "Failed delete tokenExpired from database");
                        exceptionHolder[0] = e;
                        latch.countDown();
                    });

            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException("Thread interrupted while waiting for Firebase callbacks", e);
            }

            if (exceptionHolder[0] != null) {
                throw new RuntimeException("Failed to delete token from database", exceptionHolder[0]);
            }

            return null;
        });
    }

    @Override
    public CompletableFuture<Admin> updateAdmin(Admin admin){
        CompletableFuture<Admin> future = new CompletableFuture<>();

        DatabaseReference child = database.child(admin.getId());
        child.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    Log.e("AdminRepository", "User not found");
                    future.completeExceptionally(new Exception("User tidak ditemukan"));
                    return;
                }
                child.child("password")
                        .setValue(admin.getPassword())
                        .addOnSuccessListener(aVoid ->{
                            Log.i("UserRepository", "Success Update password");
                            future.complete(admin);
                        })
                        .addOnFailureListener(e ->{
                            Log.e("UserRepository", "Failed update password");
                            future.completeExceptionally(e);
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.completeExceptionally(new Exception(error.getMessage()));
                Log.e("Firebase", "Error: " + error.getMessage());
            }
        });
        return future;
    }

}
