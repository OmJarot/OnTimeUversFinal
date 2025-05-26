package com.example.ontimeuvers.service;

import com.example.ontimeuvers.entity.Jurusan;
import com.example.ontimeuvers.entity.User;
import com.example.ontimeuvers.model.DetailUserResponse;
import com.example.ontimeuvers.model.FindDataRequest;
import com.example.ontimeuvers.model.FindDataResponse;
import com.example.ontimeuvers.model.FindMahasiswaRequest;
import com.example.ontimeuvers.model.GetMahasiswaResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AdminService {

    CompletableFuture<List<GetMahasiswaResponse>> searchMahasiswa(FindMahasiswaRequest request);

    CompletableFuture<List<FindDataResponse>> searchData(FindDataRequest request);

    CompletableFuture<List<DetailUserResponse>> getDetailUser(User user);

    CompletableFuture<List<Jurusan>> allJurusan();

}
