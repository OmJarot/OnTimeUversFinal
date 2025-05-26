package com.example.ontimeuvers.service;

import com.example.ontimeuvers.entity.Jurusan;
import com.example.ontimeuvers.entity.Matkul;
import com.example.ontimeuvers.entity.User;
import com.example.ontimeuvers.model.AddNewUserRequest;
import com.example.ontimeuvers.model.AddNewUserResponse;
import com.example.ontimeuvers.model.EditMatkulRequest;
import com.example.ontimeuvers.model.EditUserRequest;
import com.example.ontimeuvers.model.FindMahasiswaRequest;
import com.example.ontimeuvers.model.GetMahasiswaResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface DBAService {

    CompletableFuture<AddNewUserResponse> addNewMahasiswa(AddNewUserRequest user);

    CompletableFuture<String> removeMahasiswa(String nim);

    CompletableFuture<String> editMahasiswa(EditUserRequest user);

    CompletableFuture<String> editUserMatkul(EditMatkulRequest request);

    CompletableFuture<List<Jurusan>> allJurusan();

    CompletableFuture<String> removeJurusan(String jurusan, Integer angkatan);

    CompletableFuture<List<User>> searchMahasiswa(FindMahasiswaRequest request);

    CompletableFuture<Void> cekJurusan(Jurusan jurusan);

}
