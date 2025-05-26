package com.example.ontimeuvers.repository;

import com.example.ontimeuvers.entity.User;
import com.example.ontimeuvers.model.AddNewUserRequest;
import com.example.ontimeuvers.model.EditUserRequest;
import com.example.ontimeuvers.model.LoginResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface UserRepository {

    CompletableFuture<User> findUserByNimPassword(String nim, String password);

    CompletableFuture<User> findUserByNimName(String nim, String name);

    CompletableFuture<User> findUserByToken(String token);

    CompletableFuture<User> updateUser(User user);

    CompletableFuture<Void> deleteUserToken(User user);

    CompletableFuture<User> findUserByNim(String nim);

    CompletableFuture<List<User>> findAllUser();

    CompletableFuture<Void> addNewUser(User user);

    CompletableFuture<Void> removeUserByNim(String nim);

    CompletableFuture<Void> editUserByNim(EditUserRequest user);

//    CompletableFuture<List<User>> findUserByName(String name);
//
//    CompletableFuture<List<User>> findUserByNimLike(String nim);
//
//    CompletableFuture<List<User>> findUserByJurusan(String jurusan);
//
//    CompletableFuture<List<User>> findUserByAngkatan(String angkatan);
}
