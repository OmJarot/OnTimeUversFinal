package com.example.ontimeuvers.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.ontimeuvers.entity.DataKeterlambatan;
import com.example.ontimeuvers.entity.User;
import com.example.ontimeuvers.util.FirebaseUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class DataKeterlambatanRepositoryImpl implements DataKeterlambatanRepository{

    private final DatabaseReference database = FirebaseUtil.getDataKeterlambatanReference();

    private UserRepository userRepository = new UserRepositoryImpl();

    @Override
    public CompletableFuture<List<DataKeterlambatan>> getAllDataByUserCurrent(User user) {
        CompletableFuture<List<DataKeterlambatan>> future = new CompletableFuture<>();
        DatabaseReference child = database.child(user.getNim());

        child.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()){
                    Log.e("DataKeterlambatan", "Data not found");
                    future.complete(Collections.emptyList());
                    return;
                }

                List<DataKeterlambatan> data = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    DataKeterlambatan dataKeterlambatan = dataSnapshot.getValue(DataKeterlambatan.class);
                    if (dataKeterlambatan == null){
                        future.complete(Collections.emptyList());
                        return;
                    }
                    dataKeterlambatan.setUser(user);
                    data.add(dataKeterlambatan);
                    Log.i("DataKeterlambatan", "Success add data");
                }
                Log.i("DataKeterlambatan", "Success add all data");
                future.complete(data);

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
    public CompletableFuture<List<DataKeterlambatan>> getAllDataKeterlambatan() {
        CompletableFuture<List<DataKeterlambatan>> future = new CompletableFuture<>();

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<DataKeterlambatan> dataKeterlambatans = Collections.synchronizedList(new ArrayList<>());

                if (!snapshot.exists()) {
                    Log.w("UserRepository", "User not found");
                    future.completeExceptionally(new RuntimeException("No user data found"));
                    return;
                }

                List<CompletableFuture<Void>> allFutures = new ArrayList<>();

                for (DataSnapshot dataUser : snapshot.getChildren()){
                    String nim = dataUser.getKey();
                    Log.i("DataKeterlambatan", "Find data user "+nim);

                    for (DataSnapshot data : dataUser.getChildren()){
                        if (!data.exists()) continue;

                        DataKeterlambatan dataKeterlambatan = data.getValue(DataKeterlambatan.class);
                        if (dataKeterlambatan != null) {
                            CompletableFuture<Void> userFuture =
                                    userRepository.findUserByNim(nim)
                                            .thenAccept(user -> {
                                                dataKeterlambatan.setUser(user);
                                                dataKeterlambatans.add(dataKeterlambatan);
                                            })
                                            .exceptionally(ex -> {
                                                Log.w("DataKeterlambatan", "User not found: " + nim);
                                                return null;
                                            });
                            allFutures.add(userFuture);
                        }
                    }
                }
                CompletableFuture
                        .allOf(allFutures.toArray(new CompletableFuture[0]))
                        .thenRun(() -> {
                            Log.i("DataKeterlambatan", "Total data ditemukan: " + dataKeterlambatans.size());
                            future.complete(dataKeterlambatans);
                        })
                        .exceptionally(ex -> {
                            future.completeExceptionally(ex);
                            return null;
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

    @Override
    public CompletableFuture<List<DataKeterlambatan>> getAllDataByDate(LocalDate localDate) {
        CompletableFuture<List<DataKeterlambatan>> future = new CompletableFuture<>();
        List<DataKeterlambatan> resultList = Collections.synchronizedList(new ArrayList<>());

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Log.i("DataKeterlambatan", "Data kosong");
                    future.complete(Collections.emptyList());
                    return;
                }

                int totalUsers = (int) snapshot.getChildrenCount();
                AtomicInteger finishedUsers = new AtomicInteger(0);

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {

                    Log.i("DataKeterlambatan", "Find from user: "+ userSnapshot.getKey());

                    String nim = userSnapshot.getKey();

                    Query query = userSnapshot.getRef()
                            .orderByChild("waktu")
                            .startAt(localDate.toString())
                            .endAt(localDate + "\uf8ff");

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                Log.i("DataKeterlambatan", "Tidak ada data pada tanggal tersebut untuk user: " + nim);
                                if (finishedUsers.incrementAndGet() == totalUsers) {
                                    future.complete(resultList);
                                }
                                return;
                            }
                            Log.i("DataKeterlambatan", "Found data: "+localDate + " from :" + userSnapshot.getKey());
                            List<CompletableFuture<Void>> futures = new ArrayList<>();

                            for (DataSnapshot entrySnapshot : dataSnapshot.getChildren()) {
                                DataKeterlambatan data = entrySnapshot.getValue(DataKeterlambatan.class);
                                if (data != null) {
                                    CompletableFuture<Void> userFuture = userRepository.findUserByNim(nim)
                                            .thenAccept(user -> {
                                                data.setUser(user);
                                                resultList.add(data);
                                                Log.i("DataKeterlambatan", "add data: "+ nim);
                                            }).exceptionally(ex -> {
                                                Log.w("DataKeterlambatan", "User not found: " + nim);
                                                return null;
                                            });

                                    futures.add(userFuture);
                                }
                            }

                            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                                    .thenRun(() -> {
                                        if (finishedUsers.incrementAndGet() == totalUsers) {
                                            future.complete(resultList);
                                        }
                                    });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("DataKeterlambatan", "Query cancelled: " + error.getMessage());
                            if (finishedUsers.incrementAndGet() == totalUsers) {
                                future.complete(resultList);
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.completeExceptionally(error.toException());
                Log.e("Firebase", "Error: " + error.getMessage());
            }
        });

        return future;
    }



    private LocalDate toLocalDate(String waktu){
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(waktu);
            return localDateTime.toLocalDate();
        }catch (DateTimeParseException e){
            Log.e("DataKeterlambatan", "Gagal parsing waktu: " + waktu, e);
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<Void> inputDataKeterlambatan(DataKeterlambatan data){
        CompletableFuture<Void> future = new CompletableFuture<>();

        DatabaseReference child = database.child(data.getUser().getNim());
        child.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    boolean tanggalSudahAda = false;
                    for (DataSnapshot item : snapshot.getChildren()){
                        String waktu = item.child("waktu").getValue(String.class);
                        try {
                            if (waktu != null && data.getLocalDateTime().toLocalDate().equals(toLocalDate(waktu))){
                                tanggalSudahAda = true;
                                break;
                            }
                        }catch (RuntimeException e){
                            future.completeExceptionally(e);
                            return;
                        }
                    }
                    if (tanggalSudahAda){
                        future.completeExceptionally(new Exception("Data untuk tanggal ini sudah ada"));
                        Log.e("DataKeterlambatan", "Gagal: tanggal duplikat");
                        return;
                    }
                }
                Map<String, String> dataKeterlambatan = new HashMap<>();
                dataKeterlambatan.put("matkul", data.getMatkul());
                dataKeterlambatan.put("waktu", data.getWaktu());
                child.push().setValue(dataKeterlambatan).addOnSuccessListener(aVoid -> {
                            Log.i("DataKeterlambatan", "Success input data to database");
                            future.complete(null);
                        })
                        .addOnFailureListener(e -> {
                            Log.e("DataKeterlambatan", "Failed to save data: " + e.getMessage());
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

    public CompletableFuture<Long> getTotalDataUser(User user){
        CompletableFuture<Long> future = new CompletableFuture<>();

        DatabaseReference child = database.child(user.getNim());
        child.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Log.w("Firebase", "User dengan NIM " + user.getNim() + " tidak ditemukan");
                    future.complete(0L);
                    return;
                }

                long total = snapshot.getChildrenCount();
                Log.i("Firebase", "Total field dari user: " + total);
                future.complete(total);
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
