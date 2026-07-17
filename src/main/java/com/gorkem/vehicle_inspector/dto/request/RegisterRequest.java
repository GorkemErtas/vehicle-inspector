package com.gorkem.vehicle_inspector.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Ad soyad boş bırakılamaz.")
    @Size(
            min = 2,
            max = 100,
            message = "Ad soyad 2 ile 100 karakter arasında olmalıdır."
    )
    private String fullName;

    @NotBlank(message = "E-posta adresi boş bırakılamaz.")
    @Email(message = "Geçerli bir e-posta adresi girilmelidir.")
    @Size(
            max = 150,
            message = "E-posta en fazla 150 karakter olabilir."
    )
    private String email;

    @NotBlank(message = "Şifre boş bırakılamaz.")
    @Size(
            min = 8,
            max = 72,
            message = "Şifre 8 ile 72 karakter arasında olmalıdır."
    )
    private String password;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

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