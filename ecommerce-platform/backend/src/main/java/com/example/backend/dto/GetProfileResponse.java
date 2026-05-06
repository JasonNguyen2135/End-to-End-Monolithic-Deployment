package com.example.backend.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetProfileResponse {
    private String email;
    private String firstName;
    private String lastName;
    private String profileImageUrl;
}
