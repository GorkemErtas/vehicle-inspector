package com.gorkem.vehicle_inspector.dto.response;

import com.gorkem.vehicle_inspector.entity.Role;

public class UserResponse {

    private final Long id;
    private final String fullName;
    private final String email;
    private final Role role;

    public UserResponse(
            Long id,
            String fullName,
            String email,
            Role role
    ) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }
}