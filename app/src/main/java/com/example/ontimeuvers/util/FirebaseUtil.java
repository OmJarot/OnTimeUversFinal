package com.example.ontimeuvers.util;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtil {

    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static DatabaseReference getUsersReference() {
        return database.getReference("users");
    }

    public static DatabaseReference getJurusanReference() {
        return database.getReference("jurusan");
    }

    public static DatabaseReference getDataKeterlambatanReference() {
        return database.getReference("data_keterlambatan");
    }

    public static DatabaseReference getAdminReference() {
        return database.getReference("admin");
    }

}
