package io.github.yogeshsthapak.hotelbooking.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateOwnerRequest(
        @NotBlank(message = "Owner name is required") String name,
        @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email
) {}
