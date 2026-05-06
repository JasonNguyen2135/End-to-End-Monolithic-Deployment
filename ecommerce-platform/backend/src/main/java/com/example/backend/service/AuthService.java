package com.example.backend.service;

import com.example.backend.dto.*;
import com.example.backend.entity.Users;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface AuthService {
    RegisterResponse register(SignUpRequest signUpRequest, MultipartFile file) throws IOException;
    LoginResponse login(SignInRequest signInRequest);
    MessageResponse updatePassword(UpdatePasswordRequest request, String currentUserEmail);
    UpdateProfileResponse updateProfile(String userEmail, String firstName, String lastName, MultipartFile file) throws IOException;
    GetProfileResponse getUserProfile(String email);
    MessageResponse deleteCurrentUser(String email);
    List<Users> getAllUsers();
}
