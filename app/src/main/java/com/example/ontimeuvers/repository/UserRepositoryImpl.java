package com.example.ontimeuvers.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.ontimeuvers.entity.User;
import com.example.ontimeuvers.model.AddNewUserRequest;
import com.example.ontimeuvers.model.EditUserRequest;
import com.example.ontimeuvers.util.FirebaseUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;


public class UserRepositoryImpl implements UserRepository{

    private final DatabaseReference database = FirebaseUtil.getUsersReference();

    private JurusanRepositoryImpl jurusanRepository= new JurusanRepositoryImpl();

    public CompletableFuture<User> findUserByNimPassword(String nim, String password){
        CompletableFuture<User> future = new CompletableFuture<>();

        DatabaseReference child = database.child(nim);
        child.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Log.e("UserRepository", "User not found");
                    future.completeExceptionally(new Exception("User not found"));
                    return;
                }
                User user = snapshot.getValue(User.class);
//                    if (user != null && BCrypt.checkpw(password, user.getPassword())){
                if (user != null && user.getPassword().equals(password)){
                    jurusanRepository.findJurusanUsingCodeJurusan(user.getJurusanKode()).thenAccept(jurusan -> {
                        user.setJurusan(jurusan);
                        future.complete(user);
                    }).exceptionally(ex -> {
                        Log.e("UserRepository", "Failed to get jurusan: " + ex.getMessage());
                        future.completeExceptionally(ex);
                        return null;
                    });
                    Log.i("UserRepository", "Found user: " + user.getName() + ", nim: " + user.getNim());
                }else {
                    future.complete(null);
                    Log.e("UserRepository", "User is null");
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
    public CompletableFuture<User> findUserByNimName(String nim, String name) {
        CompletableFuture<User> future = new CompletableFuture<>();

        DatabaseReference child = database.child(nim);
        child.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Log.e("UserRepository", "User not found");
                    future.completeExceptionally(new Exception("User not found"));
                    return;
                }
                User user = snapshot.getValue(User.class);
//                    if (user != null && BCrypt.checkpw(password, user.getPassword())){
                if (user != null && user.getName().equals(name)){
                    jurusanRepository.findJurusanUsingCodeJurusan(user.getJurusanKode()).thenAccept(jurusan -> {
                        user.setJurusan(jurusan);
                        future.complete(user);
                    }).exceptionally(ex -> {
                        Log.e("UserRepository", "Failed to get jurusan: " + ex.getMessage());
                        future.completeExceptionally(ex);
                        return null;
                    });
                    Log.i("UserRepository", "Found user: " + user.getName() + ", nim: " + user.getNim());
                }else {
                    future.completeExceptionally(new RuntimeException("User is null"));
                    Log.e("UserRepository", "User is null");
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
    public CompletableFuture<User> findUserByToken(String token) {
        CompletableFuture<User> future = new CompletableFuture<>();

        Query query = database.orderByChild("token").equalTo(token);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Log.e("UserRepository", "User not found");
                    future.completeExceptionally(new Exception("User tidak ditemukan"));
                    return;
                }
                Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();
                if (!iterator.hasNext()) {
                    Log.e("UserRepository", "User not found");
                    future.completeExceptionally(new Exception("User not found"));
                    return;
                }
                DataSnapshot data = snapshot.getChildren().iterator().next();
                User user = data.getValue(User.class);
                if (user == null || user.getTokenExpiredAt() < System.currentTimeMillis()) {
                    Log.e("UserRepository", "Token Expired");
                    future.completeExceptionally(new Exception("Token Expired"));
                    return;
                }
                jurusanRepository.findJurusanUsingCodeJurusan(user.getJurusanKode()).thenAccept(jurusan -> {
                    user.setJurusan(jurusan);
                    future.complete(user);
                }).exceptionally(ex -> {
                    Log.e("UserRepository", "Failed to get jurusan: " + ex.getMessage());
                    future.completeExceptionally(ex);
                    return null;
                });
                Log.i("UserRepository", "Found user: " + user.getName() + ", nim: " + user.getNim());
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
    public CompletableFuture<User> findUserByNim(String nim) {
        CompletableFuture<User> future = new CompletableFuture<>();

        DatabaseReference child = database.child(nim);
        child.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Log.e("UserRepository", "User not found");
                    future.completeExceptionally(new Exception("User not found"));
                    return;
                }
                User user = snapshot.getValue(User.class);
                if (user != null){
                    jurusanRepository.findJurusanUsingCodeJurusan(user.getJurusanKode()).thenAccept(jurusan -> {
                        user.setJurusan(jurusan);
                        Log.i("UserRepository", "Found user: " + user.getName() + ", nim: " + user.getNim());
                        future.complete(user);
                    }).exceptionally(ex -> {
                        Log.e("UserRepository", "Failed to get jurusan: " + ex.getMessage());
                        future.completeExceptionally(ex);
                        return null;
                    });

                }else {
                    future.completeExceptionally(new RuntimeException("User is null"));
                    Log.e("UserRepository", "User is null");
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
    public CompletableFuture<List<User>> findAllUser() {
        CompletableFuture<List<User>> future = new CompletableFuture<>();

//        database.limitToFirst(50).addListenerForSingleValueEvent(new ValueEventListener() {
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<CompletableFuture<Void>> userFutures = new ArrayList<>();
                List<User> users = Collections.synchronizedList(new ArrayList<>());

                if (!snapshot.exists()) {
                    Log.w("UserRepository", "User not found");
                    future.completeExceptionally(new RuntimeException("No user data found"));
                    return;
                }

                for (DataSnapshot userSnapshot : snapshot.getChildren()){
                    User user = userSnapshot.getValue(User.class);
                    if (user != null){
                        CompletableFuture<Void> userFuture =
                                jurusanRepository.findJurusanUsingCodeJurusan(user.getJurusanKode())
                                        .thenAccept(user::setJurusan)
                                        .exceptionally(ex -> {
                                            Log.e("UserRepository", "Failed to get jurusan: " + ex.getMessage());
                                            return null;
                                        })
                                        .thenRun(() -> users.add(user));
                        userFutures.add(userFuture);
                    }
                }
                Log.i("UserRepository","found : "+ users.size() +" user");
                CompletableFuture.allOf(userFutures.toArray(new CompletableFuture[0]))
                        .thenRun(() -> {
                            Log.i("UserRepository", "Completed all user futures. Found " + users.size() + " user(s)");
                            future.complete(users);
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
    public CompletableFuture<User> updateUser(User user){
        CompletableFuture<User> future = new CompletableFuture<>();

        DatabaseReference child = database.child(user.getNim());
        child.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    Log.e("UserRepository", "User not found");
                    future.completeExceptionally(new Exception("User tidak ditemukan"));
                    return;
                }
                child.child("password")
                        .setValue(user.getPassword())
                        .addOnSuccessListener(aVoid ->{
                            Log.i("UserRepository", "Success Update password");
                            future.complete(user);
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
    @Override
    public CompletableFuture<Void> deleteUserToken(User user) {
        return CompletableFuture.supplyAsync(() -> {
            DatabaseReference nim = database.child(user.getNim());
            CountDownLatch latch = new CountDownLatch(2);
            final Exception[] exceptionHolder = {null};

            nim.child("token").removeValue()
                    .addOnSuccessListener(success -> {
                        Log.i("Auth", "Success delete token from database");
                        latch.countDown();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Auth", "Failed delete token from database");
                        exceptionHolder[0] = e;
                        latch.countDown();
                    });

            nim.child("tokenExpiredAt").removeValue()
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
    public CompletableFuture<Void> addNewUser(User user){
        CompletableFuture<Void> future = new CompletableFuture<>();

        DatabaseReference userByNim = database.child(user.getNim());
        userByNim.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Log.e("UserRepository","User dengan nim: "+user.getNim()+" sudah ada");
                    future.completeExceptionally(new RuntimeException("User dengan nim: "+user.getNim()+" sudah ada"));
                    return;
                }else {
                    userByNim.setValue(user).addOnSuccessListener(aVoid ->{
                        Log.i("UserRepository", "Succes add new user");
                        future.complete(aVoid);
                    }).addOnFailureListener(e -> {
                        Log.e("User repository", "Failed add new user",e);
                        future.completeExceptionally(e);
                    });
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

    public CompletableFuture<Void> removeUserByNim(String nim){
        CompletableFuture<Void> future = new CompletableFuture<>();

        DatabaseReference userNim = database.child(nim);
        userNim.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    Log.e("UserRepository", "User with nim: "+ " Not Found");
                    future.completeExceptionally(new RuntimeException("User not found"));
                    return;
                }
                userNim.removeValue().addOnSuccessListener(aVoid ->{
                    Log.i("UserRepository", "Success remove account");
                    future.complete(null);
                }).addOnFailureListener(e ->{
                    Log.e("UserRepository", "Failed remove account ",e);
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

    public CompletableFuture<Void> editUserByNim(EditUserRequest user){
        CompletableFuture<Void> future = new CompletableFuture<>();

        DatabaseReference userNim = database.child(user.getNim());

        userNim.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    Log.e("UserRepository", "User not found");
                    future.completeExceptionally(new RuntimeException("User not found"));
                    return;
                }
                Map<String, Object> userNew = new HashMap<>();
                if (user.getName() != null){
                    userNew.put("name", user.getName());
                }
                if (user.getPassword() != null){
                    userNew.put("password", user.getPassword());
                }
                if (user.getJurusan() != null){
                    userNew.put("jurusanKode", user.getJurusan().getJurusanCode());
                }
                userNim.updateChildren(userNew).addOnSuccessListener(aVoid ->{
                    Log.i("UserRepository", "Success Update User: "+user.getNim());
                    future.complete(null);
                }).addOnFailureListener(e ->{
                    Log.i("UserRepository", "Failed update user: "+userNim+" :"+e);
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

//    @Override
//    public CompletableFuture<List<User>> findUserByName(String name) {
//        CompletableFuture<List<User>> future = new CompletableFuture<>();
//
//        database.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                List<CompletableFuture<Void>> userFutures = new ArrayList<>();
//                List<User> users = Collections.synchronizedList(new ArrayList<>());
//
//                for (DataSnapshot data : snapshot.getChildren()){
//                    String nameUser = data.child("name").getValue(String.class);
//                    if (nameUser != null && nameUser.toLowerCase().contains(name.toLowerCase())){
//                        Log.i("UserRepository", "Found users");
//                        User user = data.getValue(User.class);
//
//                        if (user != null) {
//                            CompletableFuture<Void> userFuture = jurusanRepository
//                                    .findJurusanUsingCodeJurusan(user.getJurusanKode())
//                                    .thenAccept(jurusan -> {
//                                        user.setJurusan(jurusan);
//                                        Log.i("UserRepository", "Set jurusan for user: " + user.getName());
//                                    })
//                                    .exceptionally(ex -> {
//                                        Log.e("UserRepository", "Failed to get jurusan: " + ex.getMessage());
//                                        return null;
//                                    })
//                                    .thenRun(() -> users.add(user));
//
//                            userFutures.add(userFuture);
//                        }
//                    }
//                }
//                Log.i("UserRepository","Found : "+ users.size()+" user");
//                CompletableFuture.allOf(userFutures.toArray(new CompletableFuture[0]))
//                        .thenRun(() -> {
//                            Log.i("UserRepository", "Completed all user futures. Found " + users.size() + " user(s) for name: " + name);
//                            future.complete(users);
//                        });
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                future.completeExceptionally(new Exception(error.getMessage()));
//                Log.e("Firebase", "Error: " + error.getMessage());
//            }
//        });
//        return future;
//    }

//    @Override
//    public CompletableFuture<List<User>> findUserByNimLike(String nim) {
//        CompletableFuture<List<User>> future = new CompletableFuture<>();
//
//        database.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                List<CompletableFuture<Void>> userFutures = new ArrayList<>();
//                List<User> users = Collections.synchronizedList(new ArrayList<>());
//
//                for (DataSnapshot dataUser : snapshot.getChildren()){
//                    String key = dataUser.getKey();
//
//                    if (key != null && key.startsWith(nim)){
//                        Log.i("UserRepository", "found nim: "+ key);
//
//                        User user = dataUser.getValue(User.class);
//                        if (user != null){
//                            CompletableFuture<Void> userFuture = jurusanRepository
//                                    .findJurusanUsingCodeJurusan(user.getJurusanKode())
//                                    .thenAccept(jurusan -> {
//                                        user.setJurusan(jurusan);
//                                        Log.i("UserRepository", "Set jurusan for user: " + user.getName());
//                                    })
//                                    .exceptionally(ex -> {
//                                        Log.e("UserRepository", "Failed to get jurusan: " + ex.getMessage());
//                                        return null;
//                                    })
//                                    .thenRun(() -> users.add(user));
//
//                            userFutures.add(userFuture);
//                        }else {
//                            Log.w("UserRepository","Data user is null");
//                        }
//                    }
//                }
//
//                Log.i("UserRepository","Found : "+ users.size()+" user");
//                CompletableFuture.allOf(userFutures.toArray(new CompletableFuture[0]))
//                        .thenRun(() -> {
//                            Log.i("UserRepository", "Completed all user futures. Found " + users.size() + " user(s) for nim: " + nim);
//                            future.complete(users);
//                        });
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                future.completeExceptionally(new Exception(error.getMessage()));
//                Log.e("Firebase", "Error: " + error.getMessage());
//            }
//        });
//        return future;
//    }

//    @Override
//    public CompletableFuture<List<User>> findUserByJurusan(String jurusan) {
//        CompletableFuture<List<User>> future = new CompletableFuture<>();
//
//        database.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot rootSnapshot) {
//                List<CompletableFuture<Void>> allQueryFutures = new ArrayList<>();
//                List<User> users = Collections.synchronizedList(new ArrayList<>());
//                Log.i("UserRepository","Start find user with jurusan: "+jurusan);
//
//                for (DataSnapshot dataUser : rootSnapshot.getChildren()) {
//                    Query query = dataUser.getRef()
//                            .orderByChild("jurusanKode")
//                            .startAt(jurusan)
//                            .endAt(jurusan + "\uf8ff");
//
//                    CompletableFuture<Void> queryFuture = new CompletableFuture<>();
//
//
//                    query.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            List<CompletableFuture<Void>> userFutures = new ArrayList<>();
//                            Log.i("UserRepository","found "+snapshot.getChildrenCount() +" user with jurusan "+ jurusan);
//
//                            for (DataSnapshot child : snapshot.getChildren()) {
//                                User user = child.getValue(User.class);
//                                if (user != null) {
//                                    CompletableFuture<Void> userFuture = jurusanRepository
//                                            .findJurusanUsingCodeJurusan(user.getJurusanKode())
//                                            .thenAccept(user::setJurusan)
//                                            .exceptionally(ex -> {
//                                                Log.e("UserRepository", "Error get jurusan: " + ex.getMessage());
//                                                return null;
//                                            })
//                                            .thenRun(() -> users.add(user));
//                                    userFutures.add(userFuture);
//                                }
//                            }
//
//                            CompletableFuture.allOf(userFutures.toArray(new CompletableFuture[0]))
//                                    .thenRun(() -> queryFuture.complete(null));
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//                            queryFuture.completeExceptionally(new Exception(error.getMessage()));
//                        }
//                    });
//
//                    allQueryFutures.add(queryFuture);
//                }
//                Log.i("UserRepository","Found : "+ users.size()+" user");
//                CompletableFuture.allOf(allQueryFutures.toArray(new CompletableFuture[0]))
//                        .thenRun(() -> {
//                            Log.i("UserRepository", "Selesai ambil semua user dengan jurusan: " + jurusan);
//                            future.complete(users);
//                        })
//                        .exceptionally(ex -> {
//                            Log.e("UserRepository", "Gagal ambil data: " + ex.getMessage());
//                            future.completeExceptionally(ex);
//                            return null;
//                        });
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                future.completeExceptionally(new Exception(error.getMessage()));
//                Log.e("Firebase", "Error: " + error.getMessage());
//            }
//        });
//
//        return future;
//    }

//    @Override
//    public CompletableFuture<List<User>> findUserByAngkatan(String angkatan) {
//        CompletableFuture<List<User>> future = new CompletableFuture<>();
//
//        database.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot rootSnapshot) {
//                List<CompletableFuture<Void>> userFutures = new ArrayList<>();
//                List<User> users = Collections.synchronizedList(new ArrayList<>());
//                Log.i("UserRepository","Start find user with angkatan: "+ angkatan);
//
//                for (DataSnapshot dataUser : rootSnapshot.getChildren()){
//                    User user = dataUser.getValue(User.class);
//                    if (user != null && user.getJurusanKode().contains(angkatan)){
//                        CompletableFuture<Void> userFuture = jurusanRepository
//                                .findJurusanUsingCodeJurusan(user.getJurusanKode())
//                                .thenAccept(user::setJurusan)
//                                .exceptionally(ex -> {
//                                    Log.e("UserRepository", "Error get jurusan: " + ex.getMessage());
//                                    return null;
//                                })
//                                .thenRun(() -> users.add(user));
//                        userFutures.add(userFuture);
//                    }
//                }
//                Log.i("UserRepository","found : "+ users.size()+" angkatan"+angkatan);
//                CompletableFuture.allOf(userFutures.toArray(new CompletableFuture[0]))
//                        .thenRun(() -> {
//                            Log.i("UserRepository", "Completed all user futures. Found " + users.size() + " user(s) for angkatan: " + angkatan);
//                            future.complete(users);
//                        });
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                future.completeExceptionally(new Exception(error.getMessage()));
//                Log.e("Firebase", "Error: " + error.getMessage());
//            }
//        });
//
//        return future;
//    }


