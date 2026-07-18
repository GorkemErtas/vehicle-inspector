package com.gorkem.vehicle_inspector.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "E-posta adresi boş bırakılamaz.")
    @Email(message = "Geçerli bir e-posta adresi girilmelidir.")
    private String email;

    @NotBlank(message = "Şifre boş bırakılamaz.")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}