package com.example.ontimeuvers.model;

public class UpdatePasswordRequest {

    private String token;

    private String oldPassword;

    private String newPassword;

    private String retypePassword;

    public UpdatePasswordRequest() {
    }

    public UpdatePasswordRequest(String token, String oldPassword, String newPassword, String retypePassword) {
        this.token = token;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.retypePassword = retypePassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getRetypePassword() {
        return retypePassword;
    }

    public void setRetypePassword(String retypePassword) {
        this.retypePassword = retypePassword;
    }
}
