package com.example.ontimeuvers.repository;

import com.example.ontimeuvers.entity.Matkul;
import com.example.ontimeuvers.model.EditMatkulRequest;

import java.util.concurrent.CompletableFuture;

public interface MatkulRepository {

    CompletableFuture<Void> editUserMatkul(EditMatkulRequest request);

}
