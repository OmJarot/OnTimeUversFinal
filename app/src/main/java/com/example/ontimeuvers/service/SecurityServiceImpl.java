package com.example.ontimeuvers.service;

import android.util.Log;

import com.example.ontimeuvers.entity.DataKeterlambatan;
import com.example.ontimeuvers.entity.User;
import com.example.ontimeuvers.model.DataTerlambatResponse;
import com.example.ontimeuvers.model.GetMahasiswaResponse;
import com.example.ontimeuvers.model.InputDataResponse;
import com.example.ontimeuvers.model.InputManualRequest;
import com.example.ontimeuvers.repository.DataKeterlambatanRepository;
import com.example.ontimeuvers.repository.UserRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


public class SecurityServiceImpl implements SecurityService{

    private UserRepository userRepository;

    private DataKeterlambatanRepository dataRepository;

    public SecurityServiceImpl(UserRepository userRepository, DataKeterlambatanRepository dataRepository) {
        this.userRepository = userRepository;
        this.dataRepository = dataRepository;
    }

    @Override
    public CompletableFuture<InputDataResponse> inputDataManual(InputManualRequest request){
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
        return userRepository.findUserByNimName(request.getNim(), request.getName()).thenCompose(user -> {
            DataKeterlambatan data = new DataKeterlambatan();
            data.setUser(user);
            data.setWaktu(toDateTime(request.getLocalDateTime()));
            data.setMatkul(user.getMatkul().getJadwalSesi(request.getLocalDateTime()));

            return dataRepository.inputDataKeterlambatan(data).thenApply(aVoid ->
                InputDataResponse.builder()
                        .user(data.getUser())
                        .tanggal(toDate(data.getWaktu()))
                        .jam(toTime(data.getWaktu()))
                        .matkul(data.getMatkul())
                        .build()
            );

        });
//            .exceptionally(ex -> {
//            Log.w("SecurityRepository", "User Not Found");
//            return null;
//        });
    }

    private CompletableFuture<List<DataTerlambatResponse>> dataTodays = null;
    private CompletableFuture<List<DataTerlambatResponse>> getAllDataTodays(LocalDate localDate){
        return dataRepository.getAllDataByDate(localDate).thenApply(allData ->
            allData.stream()
                    .map(data -> new DataTerlambatResponse(
                            data.getUser(), data.getLocalDateTime().toLocalTime(), data.getMatkul()))
                    .sorted(Comparator.comparing(DataTerlambatResponse::getJam).reversed())
                    .collect(Collectors.toList())
        ).exceptionally(ex ->
             Collections.emptyList()
        );
    }

    private synchronized CompletableFuture<List<DataTerlambatResponse>> getData(LocalDate date){
        if (dataTodays == null){
            dataTodays = getAllDataTodays(date);
        }
        return dataTodays;
    }

    public void clearCache() {
        dataTodays = null;
    }
    @Override
    public CompletableFuture<List<DataTerlambatResponse>> getAllDataToday(LocalDate localDate){
        return getData(localDate);
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


}
