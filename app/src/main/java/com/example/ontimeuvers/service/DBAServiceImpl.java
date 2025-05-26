package com.example.ontimeuvers.service;

import android.util.Log;

import com.example.ontimeuvers.entity.Jurusan;
import com.example.ontimeuvers.entity.Matkul;
import com.example.ontimeuvers.entity.User;
import com.example.ontimeuvers.model.AddNewUserRequest;
import com.example.ontimeuvers.model.AddNewUserResponse;
import com.example.ontimeuvers.model.EditMatkulRequest;
import com.example.ontimeuvers.model.EditUserRequest;
import com.example.ontimeuvers.model.FindJurusanRequest;
import com.example.ontimeuvers.model.FindMahasiswaRequest;
import com.example.ontimeuvers.model.GetMahasiswaResponse;
import com.example.ontimeuvers.model.JurusanResponse;
import com.example.ontimeuvers.repository.DataKeterlambatanRepository;
import com.example.ontimeuvers.repository.JurusanRepository;
import com.example.ontimeuvers.repository.MatkulRepository;
import com.example.ontimeuvers.repository.UserRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DBAServiceImpl implements DBAService{

    private UserRepository userRepository;

    private MatkulRepository matkulRepository;

    private JurusanRepository jurusanRepository;

    public DBAServiceImpl(UserRepository userRepository, MatkulRepository matkulRepository, JurusanRepository jurusanRepository) {
        this.userRepository = userRepository;
        this.matkulRepository = matkulRepository;
        this.jurusanRepository = jurusanRepository;
    }

    @Override
    public CompletableFuture<AddNewUserResponse> addNewMahasiswa(AddNewUserRequest request) {
        return cekJurusan(request.getJurusan()).thenCompose(aVoid -> {
            User user = new User();
            user.setNim(request.getNim());
            user.setName(request.getName());
            user.setJurusanKode(request.getJurusan().getJurusanCode());
            user.setPassword(request.getPassword());
            return userRepository.addNewUser(user)
                    .thenApply(aVoida -> new AddNewUserResponse(user.getNim(), user.getName(), user.getJurusan()));
        });
    }

    @Override
    public CompletableFuture<String> removeMahasiswa(String nim) {
        return userRepository.removeUserByNim(nim).thenApply(aVoid -> "Success").exceptionally(ex -> {
            throw new CompletionException(ex);
        });
    }

    @Override
    public CompletableFuture<String> editMahasiswa(EditUserRequest request) {
        return userRepository.editUserByNim(request).thenApply(aVoid -> "Success");
    }

    @Override
    public CompletableFuture<String> editUserMatkul(EditMatkulRequest request) {
        return matkulRepository.editUserMatkul(request).thenApply(aVoid -> "Success").exceptionally(ex -> {
            throw new CompletionException(ex);
        });
    }

    public CompletableFuture<Jurusan> addNewJurusan(Jurusan jurusan){
        return jurusanRepository.addNewJurusan(jurusan).thenApply(aVoid -> jurusan).exceptionally(ex -> {throw new CompletionException(ex);});
    }

    private CompletableFuture<List<Jurusan>> allJurusans = null;

    private CompletableFuture<List<Jurusan>> loadAllJurusansFromRepo() {
        return jurusanRepository.getAllJurusan()
                .exceptionally(ex -> Collections.emptyList());
    }

    @Override
    public synchronized CompletableFuture<List<Jurusan>> allJurusan() {
        if (allJurusans == null) {
            allJurusans = loadAllJurusansFromRepo();
        }
        return allJurusans;
    }

    public synchronized void refreshAllJurusans() {
        allJurusans = null;
    }

    @Override
    public CompletableFuture<String> removeJurusan(String jurusan, Integer angkatan) {
        return jurusanRepository.removeJurusanByJurusanCode(Jurusan.getJurusanCodes(jurusan, angkatan)).thenApply(aVoid -> "Success").exceptionally(ex -> {
            Log.e("JurusanService", "Gagal remove jurusan",ex);
            return "gagal";
        });
    }

    public CompletableFuture<List<JurusanResponse>> filterJurusan(FindJurusanRequest request){
        return allJurusan().thenApply(all ->{
            Stream<Jurusan> stream = all.stream();

            if (request.getJurusan() != null){
                stream = stream.filter(jurusan -> jurusan.getNama().contains(request.getJurusan()));
            }

            if (request.getAngkatan() != null){
                stream = stream.filter(jurusan -> Objects.equals(jurusan.getAngkatan(), request.getAngkatan()));
            }
            return stream.map(jurusan -> new JurusanResponse(jurusan.getNama(), jurusan.getAngkatan()))
                    .sorted(
                            Comparator.comparing(JurusanResponse::getAngkatan, Comparator.reverseOrder())
                                    .thenComparing(JurusanResponse::getNama))
                    .collect(Collectors.toList());
        });
    }

    private CompletableFuture<List<User>> dataUsers = null;

    private CompletableFuture<List<User>> getAllUsers(){
        return userRepository.findAllUser().exceptionally(ex -> Collections.emptyList());
    }

    private synchronized CompletableFuture<List<User>> getData(){
        if (dataUsers == null){
            dataUsers = getAllUsers();
        }
        return dataUsers;
    }
    public void clearCache() {
        dataUsers = null;
    }
    @Override
    public CompletableFuture<List<User>> searchMahasiswa(FindMahasiswaRequest request) {
        return getData().thenApply(allUser -> {
            Stream<User> stream = allUser.stream();
            if (request.getName() != null) {

                if (request.getName().startsWith("20")) {
                    stream = stream.filter(userResponse -> userResponse.getNim() != null && userResponse.getNim() .contains(request.getName()));
                } else {
                    stream = stream.filter(userResponse -> userResponse.getName()  != null && userResponse.getName().contains(request.getName()));
                }
            }

            if (request.getJurusan() != null) {
                stream = stream.filter(userResponse -> userResponse.getJurusan() != null && userResponse.getJurusan().getNama().equals(request.getJurusan()));
            }

            if (request.getAngkatan() != null) {
                stream = stream.filter(userResponse -> userResponse.getJurusan() != null && Objects.equals(userResponse.getJurusan().getAngkatan(), request.getAngkatan()));
            }
            return stream
                    .sorted(Comparator.comparing(o -> o.getNim()))
                    .collect(Collectors.toList());
        });
    }

    @Override
    public CompletableFuture<Void> cekJurusan(Jurusan jurusan) {
        return jurusanRepository.findJurusanUsingCodeJurusan(jurusan.getJurusanCode())
                .thenAccept(jurusan1 -> {
                    if (jurusan1 == null) {
                        throw new CompletionException(new RuntimeException("Jurusan not found"));
                    }
                });
    }
}
