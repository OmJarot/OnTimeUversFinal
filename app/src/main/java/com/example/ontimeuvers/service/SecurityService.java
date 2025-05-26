package com.example.ontimeuvers.service;

import com.example.ontimeuvers.model.DataTerlambatResponse;
import com.example.ontimeuvers.model.DetailUserResponse;
import com.example.ontimeuvers.model.InputDataResponse;
import com.example.ontimeuvers.model.InputManualRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SecurityService {

    CompletableFuture<InputDataResponse> inputDataManual(InputManualRequest request);

    CompletableFuture<List<DataTerlambatResponse>> getAllDataToday(LocalDate localDate);
}
