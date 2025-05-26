package com.example.ontimeuvers.repository;

import com.example.ontimeuvers.entity.Jurusan;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface JurusanRepository {

    CompletableFuture<Jurusan> findJurusanUsingCodeJurusan(String codeJurusan);

    CompletableFuture<Void> addNewJurusan(Jurusan jurusan);

    CompletableFuture<List<Jurusan>> getAllJurusan();

    CompletableFuture<Void> removeJurusanByJurusanCode(String code);
}
