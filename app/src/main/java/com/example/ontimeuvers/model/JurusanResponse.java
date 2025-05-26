package com.example.ontimeuvers.model;

public class JurusanResponse {

    private String nama;

    private Integer angkatan;

    public JurusanResponse() {
    }

    public JurusanResponse(String nama, Integer angkatan) {
        this.nama = nama;
        this.angkatan = angkatan;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public Integer getAngkatan() {
        return angkatan;
    }

    public void setAngkatan(Integer angkatan) {
        this.angkatan = angkatan;
    }
}
