package com.example.backend.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProfileResponse {
    private String message;
    private String firstName;
    private String lastName;
    private String profileImageUrl;
}
