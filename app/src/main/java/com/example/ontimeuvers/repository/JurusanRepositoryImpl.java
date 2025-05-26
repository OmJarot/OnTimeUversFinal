package com.example.ontimeuvers.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.ontimeuvers.entity.Jurusan;
import com.example.ontimeuvers.util.FirebaseUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class JurusanRepositoryImpl implements JurusanRepository{

    private final DatabaseReference database = FirebaseUtil.getJurusanReference();

    public CompletableFuture<Jurusan> findJurusanUsingCodeJurusan(String codeJurusan){
        CompletableFuture<Jurusan> future = new CompletableFuture<>();

        DatabaseReference jurusan = database.child(codeJurusan);

        jurusan.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Log.e("JurusanRepository", "Jurusan not found");
//                    future.completeExceptionally(new Exception("Jurusan not found"));
                    future.complete(null);
                    return;
                }
                Jurusan foundJurusan = snapshot.getValue(Jurusan.class);

                if (foundJurusan == null){
                    Log.e("JurusanRepository", "Jurusan is null");
//                    future.completeExceptionally(new Exception("Jurusan not found"));
                    future.complete(null);
                    return;
                }

                Log.i("JurusanRepository", "Success get jurusan: "+ foundJurusan.getNama());
                future.complete(foundJurusan);
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
    public CompletableFuture<Void> addNewJurusan(Jurusan jurusan) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        DatabaseReference child = database.child(jurusan.getJurusanCode());

        child.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Log.e("JurusanRepository", "Jurusan sudah ada");
                    future.completeExceptionally(new RuntimeException("Jurusan sudah ada"));
                    return;
                }
                child.setValue(jurusan).addOnSuccessListener(aVoid ->{
                    future.complete(null);
                    Log.i("JurusanRepository", "Success add new jurusan");
                }).addOnFailureListener(ex -> {
                    Log.e("JurusanRepository", "Failed add new Jurusan: ",ex);
                    future.completeExceptionally(ex);
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


    public CompletableFuture<List<Jurusan>> getAllJurusan(){
        CompletableFuture<List<Jurusan>> future = new CompletableFuture<>();

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Jurusan> jurusans = new ArrayList<>();
                if (!snapshot.exists()){
                    Log.i("JurusanRepository", "Jurusan not found");
                    future.complete(Collections.emptyList());
                    return;
                }
                for (DataSnapshot dataJurusan : snapshot.getChildren()){
                    Jurusan jurusan = dataJurusan.getValue(Jurusan.class);
                    if (jurusan != null){
                        jurusans.add(jurusan);
                    }
                }
                future.complete(jurusans);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.completeExceptionally(new Exception(error.getMessage()));
                Log.e("Firebase", "Error: " + error.getMessage());
            }
        });
        return future;
    }

    public CompletableFuture<Void> removeJurusanByJurusanCode(String code){
        CompletableFuture<Void> future = new CompletableFuture<>();

        database.child(code).removeValue().addOnSuccessListener(aVoid ->{
            Log.d("JurusanRepository", "Jurusan berhasil dihapus");
            future.complete(null);
        }).addOnFailureListener(ex -> {
            Log.e("JurusanRepository", "Failed remove jurusan",ex);
            future.completeExceptionally(ex);
        });
        return future;
    }

}
