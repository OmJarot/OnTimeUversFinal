package com.example.ontimeuvers.service;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.ontimeuvers.entity.DataKeterlambatan;
import com.example.ontimeuvers.entity.User;
import com.example.ontimeuvers.model.DataTerlambatResponse;
import com.example.ontimeuvers.model.InputDataResponse;
import com.example.ontimeuvers.model.DetailUserResponse;
import com.example.ontimeuvers.model.InputDataRequest;
import com.example.ontimeuvers.model.UpdatePasswordRequest;
import com.example.ontimeuvers.model.UserResponse;
import com.example.ontimeuvers.repository.DataKeterlambatanRepository;
import com.example.ontimeuvers.repository.UserRepository;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

public class UserServiceImpl implements UserService{

    private UserRepository userRepository;

    private DataKeterlambatanRepository dataKeterlambatanRepository;


    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserServiceImpl(UserRepository userRepository, DataKeterlambatanRepository dataKeterlambatanRepository) {
        this.userRepository = userRepository;
        this.dataKeterlambatanRepository = dataKeterlambatanRepository;
    }

    @Override
    public CompletableFuture<UserResponse> getCurrent(String token) {
        return userRepository.findUserByToken(token).thenApply(user ->
                UserResponse.builder()
                        .nim(user.getNim())
                        .name(user.getName())
                        .jurusan(user.getJurusan().getNama())
                        .build()
        ).exceptionally(ex ->{
            Log.e("UserService", "User not found: " + ex.getMessage());
            throw new CompletionException(new RuntimeException("User Not found"));
        });
    }
    private CompletableFuture<List<DetailUserResponse>> dataUserCurrents = null;
    private CompletableFuture<List<DetailUserResponse>> getAllDataUserCurrents(User user){
        return dataKeterlambatanRepository.getAllDataByUserCurrent(user).thenApply(dataKeterlambatans ->{
            return dataKeterlambatans.stream()
                    .sorted(Comparator.comparing(DataKeterlambatan::getLocalDateTime).reversed())
                    .map(data -> new DetailUserResponse(toDate(data.getWaktu()), toTime(data.getWaktu()), data.getMatkul()))
                    .collect(Collectors.toList());
        }).exceptionally(ex -> Collections.emptyList());
    }

    private synchronized CompletableFuture<List<DetailUserResponse>> getData(User user){
        if (dataUserCurrents == null){
            dataUserCurrents = getAllDataUserCurrents(user);
        }
        return dataUserCurrents;
    }

    public void clearCache() {
        dataUserCurrents = null;
    }

    public CompletableFuture<List<DetailUserResponse>> getAllDataUserCurrent(String token){
        return userRepository.findUserByToken(token).thenCompose(this::getData
        );
    }

    private String toDateTime(LocalDateTime waktu){
        return waktu.toString();
    }

    private String toDate(String waktu){
        try {
            Log.i("UserService", "Convert to date");
            return LocalDateTime.parse(waktu).toLocalDate().toString();
        }
        catch (Exception e){
            Log.e("DateParse", "Gagal parsing tanggal: " + waktu, e);
            throw new RuntimeException(e);
        }
    }

    private String toTime(String waktu){
        try {
            Log.i("UserService", "Convert to time");
            LocalDateTime ldt = LocalDateTime.parse(waktu);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

            return ldt.toLocalTime().format(formatter);
        }
        catch (Exception e){
            Log.e("DateParse", "Gagal parsing tanggal: " + waktu, e);
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<InputDataResponse> inputDataKeterlambatan(InputDataRequest request, String user){
        if (request.getLocalDateTime() == null){
            return CompletableFuture.supplyAsync(() -> {
                throw new IllegalArgumentException("Tanggal/Waktu tidak boleh null");
            });
        }
        if (request.getLocalDateTime().toLocalTime().isBefore(LocalTime.of(18,30)) ||
                request.getLocalDateTime().toLocalTime().isAfter(LocalTime.of(22,1)) ||
                request.getLocalDateTime().toLocalDate().getDayOfWeek() == DayOfWeek.SATURDAY ||
                request.getLocalDateTime().toLocalDate().getDayOfWeek() == DayOfWeek.SUNDAY
        ){
            Log.e("UserService", "Belum terlambat");
            return CompletableFuture.supplyAsync(() -> {
                throw new IllegalArgumentException("Masih belum terlambat");
            });
        }
        return userRepository.findUserByToken(user).thenCompose(userToken ->{
            DataKeterlambatan data = new DataKeterlambatan();
            data.setUser(userToken);
            data.setWaktu(toDateTime(request.getLocalDateTime()));

            data.setMatkul(userToken.getMatkul().getJadwalSesi(request.getLocalDateTime()));

            return dataKeterlambatanRepository.inputDataKeterlambatan(data).thenApply(aVoid -> InputDataResponse.builder()
                    .user(data.getUser())
                    .tanggal(toDate(data.getWaktu()))
                    .jam(toTime(data.getWaktu()))
                    .matkul(data.getMatkul())
                    .build());
        });
//                .exceptionally(ex ->{
//            Log.w("UserService","User not found");
//            return null;
//        });
    }

    public CompletableFuture<String> updatePassword(UpdatePasswordRequest request) {
        return userRepository.findUserByToken(request.getToken())
                .thenCompose(user -> {
                    if (!user.getPassword().equals(request.getOldPassword())) {
                        Log.w("UserService", "Password tidak sama dengan yang lama");
                        return CompletableFuture.completedFuture("Password tidak sama dengan yang lama");
                    }

                    if (!request.getNewPassword().equals(request.getRetypePassword())) {
                        Log.w("UserService", "Password tidak sama dengan retype password");
                        return CompletableFuture.completedFuture("Password tidak sama dengan retype password");
                    }

                    user.setPassword(request.getNewPassword());

                    return userRepository.updateUser(user)
                            .thenApply(updatedUser -> {
                                Log.i("UserService", "Success update password");
                                return "Success";
                            });
                })
                .exceptionally(ex -> {
                    Log.e("UserService", "Gagal update password: " + ex.getMessage());
                    return "Gagal update password";
                });
    }
}
