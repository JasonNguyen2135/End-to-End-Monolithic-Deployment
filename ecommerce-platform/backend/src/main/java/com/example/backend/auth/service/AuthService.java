package com.example.backend.auth.service;

import com.example.backend.auth.dto.Requests.*;
import com.example.backend.auth.dto.Responses.*;
import com.example.backend.entity.Users;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface AuthService {
    RegisterResponse register(SignUpRequest signUpRequest, MultipartFile file) throws IOException;
    MessageResponse verifyEmail(String token);
    LoginResponse login(SignInRequest signInRequest);
    LoginResponse refreshToken(RefreshTokenReq refreshTokenReq);
    MessageResponse logout(String email, String refreshToken);
    MessageResponse updatePassword(UpdatePasswordRequest request, String currentUserEmail);
    UpdateProfileResponse updateProfile(String userEmail, String firstName, String lastName, MultipartFile file) throws IOException;
    GetProfileResponse getUserProfile(String email);
    MessageResponse deleteCurrentUser(String email);
    List<Users> getAllUsers();
    ForgetPasswordResponse forgotPassword(String currentEmail);
    MessageResponse resetPassword(ResetPasswordRequest request);
}
