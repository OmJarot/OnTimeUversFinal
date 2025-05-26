package com.example.ontimeuvers.model;

import java.util.Map;

public class EditMatkulRequest {

    private Map<String,Map<String,String>> matkul;

    private String nim;

    public EditMatkulRequest() {
    }

    public EditMatkulRequest(Map<String, Map<String, String>> matkul, String nim) {
        this.matkul = matkul;
        this.nim = nim;
    }

    public Map<String, Map<String, String>> getMatkul() {
        return matkul;
    }

    public void setMatkul(Map<String, Map<String, String>> matkul) {
        this.matkul = matkul;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }
}
