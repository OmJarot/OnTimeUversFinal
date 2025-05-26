package com.example.ontimeuvers.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.ontimeuvers.entity.Matkul;
import com.example.ontimeuvers.model.EditMatkulRequest;
import com.example.ontimeuvers.util.FirebaseUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class MatkulRepositoryImpl implements MatkulRepository{

    private final DatabaseReference database = FirebaseUtil.getUsersReference();

    @Override
    public CompletableFuture<Void> editUserMatkul(EditMatkulRequest request) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        DatabaseReference user = database.child(request.getNim());

        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    future.completeExceptionally(new RuntimeException("User not found"));
                    return;
                }
                DatabaseReference child = user.child("matkul").child("jadwal");

                Map<String, Object> mapObject = new HashMap<>();
                for (Map.Entry<String, Map<String, String>> entry : request.getMatkul().entrySet()) {
                    mapObject.put(entry.getKey(), entry.getValue());
                }
                child.updateChildren(mapObject).addOnSuccessListener(aVoid ->{
                    future.complete(null);
                    Log.i("MatkulRepository", "Success add matkul to user: "+request.getNim());
                }).addOnFailureListener(e -> {
                    future.completeExceptionally(e);
                    Log.e("MatkulRepository", "Failed add data to user: "+request.getNim() + "cause: "+e);
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
