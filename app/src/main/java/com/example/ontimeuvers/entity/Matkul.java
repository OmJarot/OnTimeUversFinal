package com.example.ontimeuvers.entity;

import android.util.Log;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

public class Matkul  implements Serializable {

    private Map<String, Map<String, String>> jadwal;

    public String getJadwalSesi(LocalDateTime localDateTime) {
        DayOfWeek day = localDateTime.getDayOfWeek();
        LocalTime time = localDateTime.toLocalTime();

        String hariKey = mapDayToKey(day);
        Log.i("Matkul", "Search matkul for: "+hariKey);

        if (hariKey == null || jadwal == null || !jadwal.containsKey(hariKey)) {
            Log.w("Matkul", "Matkul is null for today: "+hariKey);
            return null;
        }

        Map<String, String> sesiMap = jadwal.get(hariKey);
        if (sesiMap == null) {
            Log.w("Matkul", "Matkul sesi is null");
            return null;
        };

        if (time.isAfter(LocalTime.of(18, 30)) && time.isBefore(LocalTime.of(20, 1))) {
            Log.i("Matkul", "Found matkul in sesi1: "+sesiMap.get("sesi1"));
            return sesiMap.get("sesi1");
        } else if (time.isAfter(LocalTime.of(20, 0)) && time.isBefore(LocalTime.of(22, 1))) {
            Log.i("Matkul", "Found matkul in sesi1: "+sesiMap.get("sesi2"));
            return sesiMap.get("sesi2");
        }
        Log.i("Matkul", "Matkul is null");
        return null;
    }

    private String mapDayToKey(DayOfWeek day) {
        switch (day) {
            case MONDAY: return "Senin";
            case TUESDAY: return "Selasa";
            case WEDNESDAY: return "Rabu";
            case THURSDAY: return "Kamis";
            case FRIDAY: return "Jumat";
            default: return null;
        }
    }

    public Matkul() {
    }

    public Matkul(Map<String, Map<String, String>> jadwal) {
        this.jadwal = jadwal;
    }

    public Map<String, Map<String, String>> getJadwal() {
        return jadwal;
    }

    public void setJadwal(Map<String, Map<String, String>> jadwal) {
        this.jadwal = jadwal;
    }

}
