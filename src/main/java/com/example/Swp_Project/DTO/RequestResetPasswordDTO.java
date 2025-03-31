package com.example.Swp_Project.DTO;

public class RequestResetPasswordDTO {
    private String newPassword;
    private String confirmNewpassword;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmNewpassword() {
        return confirmNewpassword;
    }

    public void setConfirmNewpassword(String confirmNewpassword) {
        this.confirmNewpassword = confirmNewpassword;
    }
}
