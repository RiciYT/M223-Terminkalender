package com.example.reservations.web.dto;

import jakarta.validation.constraints.Email;

public class ParticipantForm {

    private String name;

    @Email(message = "Email should be valid")
    private String email;

    public ParticipantForm() {
    }

    public ParticipantForm(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
