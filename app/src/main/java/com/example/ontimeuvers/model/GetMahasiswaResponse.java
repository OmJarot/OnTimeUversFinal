package com.example.ontimeuvers.model;

import com.example.ontimeuvers.entity.User;

public class GetMahasiswaResponse {

    private User user;

    private Long total;

    public GetMahasiswaResponse() {
    }

    public GetMahasiswaResponse(User user, Long total) {
        this.user = user;
        this.total = total;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
