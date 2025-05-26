package com.example.ontimeuvers.entity;

import java.io.Serializable;

public class Jurusan implements Serializable {

    private String nama;

    private Integer angkatan;

    public String getJurusanCode(){
        return nama+ " "+angkatan;
    }

    public static String getJurusanCodes(String namaJurusan, Integer angkatan){
        return namaJurusan+" "+angkatan;
    }

//    public String getJurusanCode(){
//        if (nama == null || nama.trim().isEmpty()) return "";
//
//        StringBuilder kode = new StringBuilder();
//
//        for (String n : nama.split(" ")) {
//            if (!n.isEmpty()) {
//                kode.append(n.charAt(0));
//            }
//        }
//        kode.append(angkatan);
//        return kode.toString();
//    }

//    public static String getJurusanCodes(String namaJurusan, Integer angkatan){
//        if (namaJurusan == null || namaJurusan.trim().isEmpty()) return "";
//
//        StringBuilder kode = new StringBuilder();
//
//        for (String n : namaJurusan.split(" ")) {
//            if (!n.isEmpty()) {
//                kode.append(n.charAt(0));
//            }
//        }
//        kode.append(angkatan);
//        return kode.toString();
//    }

    public Jurusan() {
    }

    public Jurusan(String nama, Integer angkatan) {
        this.nama = nama;
        this.angkatan = angkatan;
    }

    public Integer getAngkatan() {
        return angkatan;
    }

    public void setAngkatan(Integer angkatan) {
        this.angkatan = angkatan;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }
}
