package com.example.ontimeuvers.entity;

import com.google.firebase.database.Exclude;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataKeterlambatan {

    @Exclude
    private User user;

    private String waktu;

    private String matkul;

    public LocalDateTime getLocalDateTime(){
        return LocalDateTime.parse(waktu);
    }
}
