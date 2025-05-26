package com.example.ontimeuvers.model;

public class FindMahasiswaRequest {

    private String name;

    private String jurusan;

    private Integer angkatan;

    public FindMahasiswaRequest() {
    }

    public FindMahasiswaRequest(String name, String jurusan, Integer angkatan) {
        this.name = name;
        this.jurusan = jurusan;
        this.angkatan = angkatan;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJurusan() {
        return jurusan;
    }

    public void setJurusan(String jurusan) {
        this.jurusan = jurusan;
    }

    public Integer getAngkatan() {
        return angkatan;
    }

    public void setAngkatan(Integer angkatan) {
        this.angkatan = angkatan;
    }
}
