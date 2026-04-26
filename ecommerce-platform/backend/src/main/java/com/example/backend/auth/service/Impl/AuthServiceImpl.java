package com.example.backend.auth.service.Impl;

import com.example.backend.auth.dto.Requests.*;
import com.example.backend.auth.dto.Responses.*;
import com.example.backend.auth.exception.*;
import com.example.backend.entity.*;
import com.example.backend.exception.*;
import com.example.backend.auth.service.AuthService;
import com.example.backend.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@SuppressWarnings("ALL")
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsersRepo usersRepo;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepo verificationTokenRepo;
    private final AuthenticationManager authenticationManager;
    private final JWTserviceImpl jwtService;
    private final OTPRepository otpRepository;
    private final PasswordResetAttemptRepository attemptRepo;

    @Override
    @Transactional
    public RegisterResponse register(SignUpRequest signUpRequest , MultipartFile file) throws IOException {
        if (usersRepo.findByEmail(signUpRequest.getEmail()).isPresent()) {
            throw new RuntimeException("The new email is already in use.");
        }
        Role userRole = signUpRequest.getRole() != null ? signUpRequest.getRole() : Role.ROLE_USER;
        
        Users user = Users.builder()
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .email(signUpRequest.getEmail())
                .role(userRole)
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .enabled(true)
                .emailVerified(true)
                .createdAt(LocalDateTime.now())
                .profileImageUrl("https://via.placeholder.com/150")
                .build();

        usersRepo.save(user);

        return new RegisterResponse("User registered successfully." ,"SUCCESS");
    }

    @Override
    @Transactional
    public MessageResponse verifyEmail(String token) {
        return new MessageResponse("Email verification is disabled.");
    }

    @Override
    public LoginResponse login(SignInRequest signInRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(signInRequest.getEmail(), signInRequest.getPassword())
            );
        } catch (AuthenticationException ex) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        Users user = usersRepo.findByEmail(signInRequest.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new LoginResponse (
            "Login successful",
            token, refreshToken,
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getProfileImageUrl(),
            user.getRole()
        );
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenReq refreshTokenReq) {
        String email;
        try {
            email = jwtService.extractUsername(refreshTokenReq.getToken());
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        Users user = usersRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        if (!jwtService.validateToken(refreshTokenReq.getToken(), user)) {
            throw new InvalidTokenException("Refresh token expired or invalid");
        }

        String newAccessToken = jwtService.generateToken(user);

        return new LoginResponse(
                "Token refreshed successfully",
                newAccessToken,
                refreshTokenReq.getToken(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getProfileImageUrl(),
                user.getRole()
        );
    }

    @Override
    public MessageResponse logout(String email, String refreshToken) {
        return new MessageResponse("Logged out successfully");
    }

    @Override
    @Transactional
    public MessageResponse updatePassword(UpdatePasswordRequest request, String currentUserEmail) {
        Users user = usersRepo.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        usersRepo.save(user);

        return new MessageResponse("Password updated successfully");
    }

    @Override
    public UpdateProfileResponse updateProfile(String userEmail, String firstName, String lastName, MultipartFile file) throws IOException {
        Users user = usersRepo.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (firstName != null) user.setFirstName(firstName);
        if (lastName != null) user.setLastName(lastName);

        usersRepo.save(user);

        return new UpdateProfileResponse(
                "Profile updated successfully",
                user.getFirstName(),
                user.getLastName(),
                user.getProfileImageUrl()
        );
    }

    public GetProfileResponse getUserProfile(String email) {
        Users user = usersRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        return new GetProfileResponse(
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getProfileImageUrl()
        );
    }

    @Transactional
    public MessageResponse deleteCurrentUser(String email) {
        Users user = usersRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        verificationTokenRepo.deleteByUser(user);
        usersRepo.delete(user);
        return new MessageResponse("User Deleted Successfully");
    }

    public @Nullable List<Users> getAllUsers() {
        return usersRepo.findAll();
    }

    @Transactional
    @Override
    public ForgetPasswordResponse forgotPassword(String currentEmail) {
        return new ForgetPasswordResponse("Feature disabled: Email service removed", "");
    }

    @Transactional
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        return new MessageResponse("Feature disabled: Email service removed");
    }
}
