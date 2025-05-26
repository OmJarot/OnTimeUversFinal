package com.example.ontimeuvers.repository;

import com.example.ontimeuvers.entity.DataKeterlambatan;
import com.example.ontimeuvers.entity.User;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface DataKeterlambatanRepository {

    CompletableFuture<List<DataKeterlambatan>> getAllDataByUserCurrent(User user);

    CompletableFuture<List<DataKeterlambatan>> getAllDataKeterlambatan();

    CompletableFuture<List<DataKeterlambatan>> getAllDataByDate(LocalDate localDate);

    CompletableFuture<Void> inputDataKeterlambatan(DataKeterlambatan data);

    CompletableFuture<Long> getTotalDataUser(User user);
}
