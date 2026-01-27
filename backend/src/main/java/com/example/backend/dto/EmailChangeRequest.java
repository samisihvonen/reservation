// File: src/main/java/com/example/backend/dto/EmailChangeRequest.java

package com.example.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmailChangeRequest {

    @NotBlank(message = "Uusi sähköposti on pakollinen")
    @Email(message = "Kelvollinen sähköpostiosoite vaaditaan")
    private String newEmail;

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }
}